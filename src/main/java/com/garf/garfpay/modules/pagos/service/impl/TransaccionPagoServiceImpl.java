package com.garf.garfpay.modules.pagos.service.impl;

import com.garf.garfpay.modules.auditoria.service.IAuditoriaService;
import com.garf.garfpay.modules.contabilidad.entity.EventoTransaccion;
import com.garf.garfpay.modules.contabilidad.repository.EventoTransaccionRepository;
import com.garf.garfpay.modules.contabilidad.service.ITarifarioService;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.notificaciones.event.TransaccionCompletadaEvent;
import com.garf.garfpay.modules.pagos.dto.request.ProcesarPagoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.TransaccionResponseDTO;
import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobro;
import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobroId;
import com.garf.garfpay.modules.pagos.entity.SolicitudCobro;
import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import com.garf.garfpay.modules.pagos.enums.EstadoDestinoCobro;
import com.garf.garfpay.modules.pagos.enums.EstadoTransaccion;
import com.garf.garfpay.modules.pagos.gateway.IPasarelaPagoGateway;
import com.garf.garfpay.modules.pagos.gateway.PasarelaGatewayResolver;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.gateway.dto.SolicitudCargoGatewayDTO;
import com.garf.garfpay.modules.pagos.mapper.PagosMapper;
import com.garf.garfpay.modules.pagos.repository.DestinoSolicitudCobroRepository;
import com.garf.garfpay.modules.pagos.repository.SolicitudCobroRepository;
import com.garf.garfpay.modules.pagos.repository.TransaccionPagoRepository;
import com.garf.garfpay.modules.pagos.service.ITransaccionPagoService;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import com.garf.garfpay.shared.util.MoneyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionPagoServiceImpl implements ITransaccionPagoService {

    private static final BigDecimal COMISION_PLATAFORMA_PORCENTAJE = new BigDecimal("0.01");

    private final TransaccionPagoRepository transaccionRepository;
    private final SolicitudCobroRepository solicitudCobroRepository;
    private final DestinoSolicitudCobroRepository destinoRepository;
    private final UsuarioAppRepository usuarioRepository;
    private final EventoTransaccionRepository eventoTransaccionRepository;
    private final PagosMapper pagosMapper;
    private final ITarifarioService tarifarioService;
    private final PasarelaGatewayResolver gatewayResolver;
    private final IAuditoriaService auditoriaService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public TransaccionResponseDTO procesarPago(UUID usuarioPagadorId, ProcesarPagoRequestDTO request) {

        // Idempotencia — evita cobros dobles
        if (transaccionRepository.existsByClaveIdempotencia(request.claveIdempotencia())) {
            throw new BusinessRuleException("Esta transacción ya fue procesada anteriormente.");
        }

        UsuarioApp pagador = usuarioRepository.findById(usuarioPagadorId)
                .orElseThrow(() -> new BusinessRuleException("Usuario pagador no encontrado."));

        SolicitudCobro solicitud = solicitudCobroRepository.findById(request.solicitudCobroId())
                .orElseThrow(() -> new BusinessRuleException("La solicitud de cobro no existe."));

        DestinoSolicitudCobro destino = destinoRepository
                .findById(new DestinoSolicitudCobroId(solicitud.getSolicitudCobroId(), pagador.getUsuarioId()))
                .orElseThrow(() -> new BusinessRuleException("No tienes una deuda asignada para esta solicitud."));

        if (destino.getEstado() == EstadoDestinoCobro.PAGADO) {
            throw new BusinessRuleException("Esta deuda ya ha sido pagada.");
        }

        // Comisiones REALES desde el Tarifario vigente de la organización
        UUID organizacionId = solicitud.getOrganizacion().getOrganizacionId();
        OffsetDateTime ahora = OffsetDateTime.now();

        BigDecimal comisionPasarela = tarifarioService.calcularComision(
                organizacionId, request.proveedor(), request.monto(), ahora);
        BigDecimal comisionPlataforma = MoneyUtils.formatAmount(
                request.monto().multiply(COMISION_PLATAFORMA_PORCENTAJE));

        // Se registra la transacción en estado PROCESANDO antes de llamar al PSP,
        //    para que quede trazabilidad incluso si la llamada al proveedor falla.
        String idCorrelacion = "COR-" + UUID.randomUUID();

        TransaccionPago transaccion = TransaccionPago.builder()
                .solicitudCobro(solicitud)
                .usuarioPagador(pagador)
                .cuentaLiquidacion(solicitud.getOrganizacion() != null ? null : null)
                .proveedor(request.proveedor())
                .claveIdempotencia(request.claveIdempotencia())
                .monto(request.monto())
                .comisionPasarela(comisionPasarela)
                .comisionPlataforma(comisionPlataforma)
                .estado(EstadoTransaccion.PROCESANDO)
                .metadatos(request.metadatos())
                .idCorrelacion(idCorrelacion)
                .build();

        transaccion = transaccionRepository.save(transaccion);

        // 4. Llamada real al PSP a través del puerto (sin strings "SIMULACION-")
        IPasarelaPagoGateway gateway = gatewayResolver.resolver(request.proveedor());

        SolicitudCargoGatewayDTO solicitudGateway = new SolicitudCargoGatewayDTO(
                transaccion.getTransaccionPagoId(),
                request.claveIdempotencia(),
                request.monto(),
                transaccion.getMoneda(),
                request.metadatos(),
                idCorrelacion);

        ResultadoGatewayDTO resultado = gateway.cobrar(solicitudGateway);

        registrarResultadoDelProveedor(transaccion, resultado);

        if (!resultado.exitoso()) {
            transaccion.setEstado(EstadoTransaccion.FALLIDO);
            transaccion.setMotivoFallo(resultado.motivoFallo());
            transaccionRepository.save(transaccion);

            auditoriaService.registrarAccionInterna(
                    pagador.getUsuarioId(), "PAGOS", "PAGO_FALLIDO", "TransaccionPago",
                    transaccion.getTransaccionPagoId(), null,
                    Map.of("motivo", String.valueOf(resultado.motivoFallo())), null, null);

            throw new BusinessRuleException("El proveedor de pago rechazó la operación: " + resultado.motivoFallo());
        }

        transaccion.setEstado(EstadoTransaccion.COMPLETADO);
        transaccion.setIdTransaccionProveedor(resultado.idTransaccionProveedor());
        transaccion.setReferenciaProveedor(resultado.referenciaProveedor());
        transaccion.setIdTraza(resultado.idTraza());
        transaccion = transaccionRepository.save(transaccion);

        //  Deuda saldada
        destino.setEstado(EstadoDestinoCobro.PAGADO);
        destino.setPagadoEl(ahora);
        destinoRepository.save(destino);

        EventoTransaccion evento = EventoTransaccion.builder()
                .transaccionPago(transaccion)
                .codigoEvento("PAGO_COMPLETADO")
                .descripcion("Pago procesado exitosamente vía " + request.proveedor())
                .payload(Map.of(
                        "comisionPasarela", comisionPasarela,
                        "comisionPlataforma", comisionPlataforma,
                        "idTransaccionProveedor", String.valueOf(resultado.idTransaccionProveedor())))
                .build();
        eventoTransaccionRepository.save(evento);

        // Auditoría genérica
        auditoriaService.registrarAccionInterna(
                pagador.getUsuarioId(), "PAGOS", "PAGO_COMPLETADO", "TransaccionPago",
                transaccion.getTransaccionPagoId(), null,
                Map.of("monto", request.monto(), "proveedor", request.proveedor().name()), null, null);

        // Evento de dominio para disparar webhooks salientes
        eventPublisher.publishEvent(new TransaccionCompletadaEvent(this, transaccion.getTransaccionPagoId(), organizacionId));

        log.info("Transacción {} completada para organización {} por PEN {}",
                transaccion.getTransaccionPagoId(), organizacionId, request.monto());

        return pagosMapper.toTransaccionResponse(transaccion);
    }

    private void registrarResultadoDelProveedor(TransaccionPago transaccion, ResultadoGatewayDTO resultado) {
        Map<String, Object> respuestaProveedor = new HashMap<>();
        if (resultado.respuestaCruda() != null) {
            respuestaProveedor.putAll(resultado.respuestaCruda());
        }
        respuestaProveedor.put("exitoso", resultado.exitoso());
        transaccion.setRespuestaProveedor(respuestaProveedor);
    }
}