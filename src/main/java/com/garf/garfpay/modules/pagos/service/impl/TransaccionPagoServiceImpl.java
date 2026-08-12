package com.garf.garfpay.modules.pagos.service.impl;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.pagos.dto.request.ProcesarPagoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.TransaccionResponseDTO;
import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobro;
import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobroId;
import com.garf.garfpay.modules.pagos.entity.SolicitudCobro;
import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import com.garf.garfpay.modules.pagos.enums.EstadoDestinoCobro;
import com.garf.garfpay.modules.pagos.enums.EstadoTransaccion;
import com.garf.garfpay.modules.pagos.mapper.PagosMapper;
import com.garf.garfpay.modules.pagos.repository.DestinoSolicitudCobroRepository;
import com.garf.garfpay.modules.pagos.repository.SolicitudCobroRepository;
import com.garf.garfpay.modules.pagos.repository.TransaccionPagoRepository;
import com.garf.garfpay.modules.pagos.service.ITransaccionPagoService;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransaccionPagoServiceImpl implements ITransaccionPagoService {

    private final TransaccionPagoRepository transaccionRepository;
    private final SolicitudCobroRepository solicitudCobroRepository;
    private final DestinoSolicitudCobroRepository destinoRepository;
    private final UsuarioAppRepository usuarioRepository;
    private final PagosMapper pagosMapper;

    @Override
    @Transactional
    public TransaccionResponseDTO procesarPago(UUID usuarioPagadorId, ProcesarPagoRequestDTO request) {

        // Idempotencia Evitar cobros dobles
        if (transaccionRepository.existsByClaveIdempotencia(request.claveIdempotencia())) {
            throw new BusinessRuleException("Esta transacción ya fue procesada anteriormente.");
        }

        UsuarioApp pagador = usuarioRepository.findById(usuarioPagadorId)
                .orElseThrow(() -> new BusinessRuleException("Usuario pagador no encontrado."));

        SolicitudCobro solicitud = solicitudCobroRepository.findById(request.solicitudCobroId())
                .orElseThrow(() -> new BusinessRuleException("La solicitud de cobro no existe."));

        DestinoSolicitudCobro destino = destinoRepository.findById(new DestinoSolicitudCobroId(solicitud.getSolicitudCobroId(), pagador.getUsuarioId()))
                .orElseThrow(() -> new BusinessRuleException("No tienes una deuda asignada para esta solicitud."));

        if (destino.getEstado() == EstadoDestinoCobro.PAGADO) {
            throw new BusinessRuleException("Esta deuda ya ha sido pagada.");
        }

        // Cálculo de comisiones
        BigDecimal comisionPasarela = request.monto().multiply(new BigDecimal("0.0399"));
        BigDecimal comisionPlataforma = request.monto().multiply(new BigDecimal("0.01"));

        // ====================================================================
        // AQUÍ VA LA INTEGRACIÓN REAL CON NIUBIZ O CULQI (Llamada HTTP)
        // String tokenTransaccion = niubizClient.cobrar(request.metadatos());
        // ====================================================================

        // Simulamos éxito
        TransaccionPago transaccion = TransaccionPago.builder()
                .solicitudCobro(solicitud)
                .usuarioPagador(pagador)
                .proveedor(request.proveedor())
                .claveIdempotencia(request.claveIdempotencia())
                .monto(request.monto())
                .comisionPasarela(comisionPasarela)
                .comisionPlataforma(comisionPlataforma)
                .estado(EstadoTransaccion.COMPLETADO)
                .metadatos(request.metadatos())
                .referenciaProveedor("SIMULACION-" + UUID.randomUUID().toString().substring(0, 8))
                .build();

        transaccion = transaccionRepository.save(transaccion);

        // Actualizamos la deuda a PAGADO
        destino.setEstado(EstadoDestinoCobro.PAGADO);
        destino.setPagadoEl(OffsetDateTime.now());
        destinoRepository.save(destino);

        return pagosMapper.toTransaccionResponse(transaccion);
    }
}