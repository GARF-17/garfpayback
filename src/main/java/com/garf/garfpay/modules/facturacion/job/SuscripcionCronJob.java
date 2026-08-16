package com.garf.garfpay.modules.facturacion.job;

import com.garf.garfpay.modules.facturacion.entity.SuscripcionOrganizacion;
import com.garf.garfpay.modules.facturacion.enums.FrecuenciaSuscripcion;
import com.garf.garfpay.modules.facturacion.repository.SuscripcionOrganizacionRepository;
import com.garf.garfpay.modules.notificaciones.event.SuscripcionSuspendidaEvent;
import com.garf.garfpay.modules.pagos.entity.MetodoPagoGuardado;
import com.garf.garfpay.modules.pagos.gateway.IPasarelaPagoGateway;
import com.garf.garfpay.modules.pagos.gateway.PasarelaGatewayResolver;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.repository.MetodoPagoGuardadoRepository;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.enums.EstadoOrganizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuscripcionCronJob {

    private final SuscripcionOrganizacionRepository suscripcionRepository;
    private final OrganizacionRepository organizacionRepository;
    private final MetodoPagoGuardadoRepository metodoPagoRepository;
    private final PasarelaGatewayResolver gatewayResolver;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void procesarCobrosDeSuscripcion() {
        log.info("Iniciando motor de facturación automático (Cron Job)...");

        LocalDate hoy = LocalDate.now();
        List<SuscripcionOrganizacion> suscripcionesVencidas = suscripcionRepository.buscarSuscripcionesPorVencer(hoy);

        if (suscripcionesVencidas.isEmpty()) {
            log.info("No hay suscripciones por cobrar el día de hoy.");
            return;
        }

        for (SuscripcionOrganizacion suscripcion : suscripcionesVencidas) {
            procesarUnaSuscripcion(suscripcion);
        }

        log.info("Motor de facturación terminó su ejecución.");
    }

    private void procesarUnaSuscripcion(SuscripcionOrganizacion suscripcion) {
        Organizacion organizacion = suscripcion.getOrganizacion();

        try {
            Optional<MetodoPagoGuardado> metodoPagoOpt = metodoPagoRepository
                    .findByOrganizacion_OrganizacionIdAndEsPredeterminadoTrueAndEstaActivoTrue(organizacion.getOrganizacionId());

            if (metodoPagoOpt.isEmpty()) {
                log.warn("Organización {} no tiene método de pago guardado. Se suspende por falta de medio de cobro.",
                        organizacion.getRazonSocial());
                bloquearOrganizacionPorFaltaDePago(suscripcion, organizacion, "Sin método de pago registrado");
                return;
            }

            MetodoPagoGuardado metodoPago = metodoPagoOpt.get();
            IPasarelaPagoGateway gateway = gatewayResolver.resolver(metodoPago.getProveedor());

            String claveIdempotencia = "SUSC-" + suscripcion.getSuscripcionOrganizacionId() + "-" + LocalDate.now();

            log.info("Cobrando {} {} a la organización: {} vía {}",
                    suscripcion.getPlanSuscripcion().getPrecio(), "USD",
                    organizacion.getRazonSocial(), metodoPago.getProveedor());

            ResultadoGatewayDTO resultado = gateway.cobrarConTokenGuardado(
                    metodoPago.getTokenProveedor(),
                    suscripcion.getPlanSuscripcion().getPrecio(),
                    "USD",
                    claveIdempotencia);

            if (resultado.exitoso()) {
                extenderSuscripcion(suscripcion);
                suscripcionRepository.save(suscripcion);
                log.info("Cobro exitoso (ref={}). Suscripción extendida para: {}",
                        resultado.referenciaProveedor(), organizacion.getRazonSocial());
            } else {
                log.warn("Cobro rechazado para {}: {}", organizacion.getRazonSocial(), resultado.motivoFallo());
                bloquearOrganizacionPorFaltaDePago(suscripcion, organizacion, resultado.motivoFallo());
            }

        } catch (Exception e) {
            log.error("Error al procesar el cobro de la organización {}: {}",
                    organizacion.getOrganizacionId(), e.getMessage(), e);
        }
    }

    private void extenderSuscripcion(SuscripcionOrganizacion suscripcion) {
        FrecuenciaSuscripcion frecuencia = suscripcion.getPlanSuscripcion().getFrecuencia();
        LocalDate baseDeCalculo = suscripcion.getTerminaEl();
        switch (frecuencia) {
            case SEMANAL -> suscripcion.setTerminaEl(baseDeCalculo.plusWeeks(1));
            case QUINCENAL -> suscripcion.setTerminaEl(baseDeCalculo.plusWeeks(2));
            case MENSUAL -> suscripcion.setTerminaEl(baseDeCalculo.plusMonths(1));
            case ANUAL -> suscripcion.setTerminaEl(baseDeCalculo.plusYears(1));
        }
    }

    private void bloquearOrganizacionPorFaltaDePago(SuscripcionOrganizacion suscripcion, Organizacion organizacion, String motivo) {
        suscripcion.setEstaActiva(false);
        suscripcionRepository.save(suscripcion);

        organizacion.setEstado(EstadoOrganizacion.SUSPENDIDA);
        organizacionRepository.save(organizacion);

        log.warn("Organización {} SUSPENDIDA por falta de pago. Motivo: {}", organizacion.getRazonSocial(), motivo);

        eventPublisher.publishEvent(new SuscripcionSuspendidaEvent(
                this,
                suscripcion.getSuscripcionOrganizacionId(),
                organizacion.getOrganizacionId(),
                motivo
        ));
    }
}