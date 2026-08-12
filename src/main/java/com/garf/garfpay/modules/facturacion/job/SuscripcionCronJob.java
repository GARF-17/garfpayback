package com.garf.garfpay.modules.facturacion.job;

import com.garf.garfpay.modules.facturacion.entity.SuscripcionOrganizacion;
import com.garf.garfpay.modules.facturacion.enums.FrecuenciaSuscripcion;
import com.garf.garfpay.modules.facturacion.repository.SuscripcionOrganizacionRepository;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.enums.EstadoOrganizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class SuscripcionCronJob {

    private final SuscripcionOrganizacionRepository suscripcionRepository;
    private final OrganizacionRepository organizacionRepository;

    // Se ejecuta todos los días a las 2:00 AM hora del servidor
    // Expresión Cron: Segundos, Minutos, Horas, Día, Mes, Día de la semana
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
            Organizacion organizacion = suscripcion.getOrganizacion();

            try {
                log.info("Intentando cobrar USD {} a la organización: {}",
                        suscripcion.getPlanSuscripcion().getPrecio(), organizacion.getRazonSocial());

                // AQUÍ IRÍA LA INTEGRACIÓN CON NIUBIZ, CULQI O STRIPE
                // boolean cobroExitoso = pasarelaService.cobrarTarjeta(organizacion.getTokenTarjeta(), precio);
                boolean cobroExitoso = true; // Simulamos que el cobro pasó con éxito

                if (cobroExitoso) {
                    // Si pagaron, extendemos su suscripción
                    extenderSuscripcion(suscripcion);
                    suscripcionRepository.save(suscripcion);
                    log.info("Cobro exitoso. Suscripción extendida para: {}", organizacion.getRazonSocial());
                } else {
                    // Si falla la tarjeta (no hay fondos, tarjeta bloqueada)
                    bloquearOrganizacionPorFaltaDePago(suscripcion, organizacion);
                }

            } catch (Exception e) {
                log.error("Error al procesar el cobro de la organización {}: {}", organizacion.getOrganizacionId(), e.getMessage());
                // En la vida real, aquí envías un correo al equipo de soporte y reintentas mañana.
            }
        }

        log.info("Motor de facturación terminó su ejecución.");
    }

    private void extenderSuscripcion(SuscripcionOrganizacion suscripcion) {
        FrecuenciaSuscripcion frecuencia = suscripcion.getPlanSuscripcion().getFrecuencia();

        // Si era su prueba de 3 días (Mensual), ahora le sumamos 1 MES real.
        if (frecuencia == FrecuenciaSuscripcion.MENSUAL) {
            suscripcion.setTerminaEl(suscripcion.getTerminaEl().plusMonths(1));
        } else if (frecuencia == FrecuenciaSuscripcion.ANUAL) {
            suscripcion.setTerminaEl(suscripcion.getTerminaEl().plusYears(1));
        }
    }

    private void bloquearOrganizacionPorFaltaDePago(SuscripcionOrganizacion suscripcion, Organizacion organizacion) {
        suscripcion.setEstaActiva(false);
        suscripcionRepository.save(suscripcion);

        // Pasamos la organización a SUSPENDIDA para que no pueda lanzar más links de cobro
        organizacion.setEstado(EstadoOrganizacion.SUSPENDIDA);
        organizacionRepository.save(organizacion);

        log.warn("Organización {} SUSPENDIDA por falta de pago.", organizacion.getRazonSocial());
        // Aquí dispararíamos un correo: "Tu pago de $3.90 rebotó, actualiza tu tarjeta para seguir cobrando".
    }
}