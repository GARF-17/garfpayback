package com.garf.garfpay.modules.pagos.service.impl;

import com.garf.garfpay.modules.auditoria.service.IAuditoriaService;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.pagos.dto.request.SolicitarReembolsoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.ReembolsoResponseDTO;
import com.garf.garfpay.modules.pagos.entity.Reembolso;
import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import com.garf.garfpay.modules.pagos.enums.EstadoReembolso;
import com.garf.garfpay.modules.pagos.enums.EstadoTransaccion;
import com.garf.garfpay.modules.pagos.gateway.IPasarelaPagoGateway;
import com.garf.garfpay.modules.pagos.gateway.PasarelaGatewayResolver;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.mapper.PagosMapper;
import com.garf.garfpay.modules.pagos.repository.ReembolsoRepository;
import com.garf.garfpay.modules.pagos.repository.TransaccionPagoRepository;
import com.garf.garfpay.modules.pagos.service.IReembolsoService;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import com.garf.garfpay.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReembolsoServiceImpl implements IReembolsoService {

    private final ReembolsoRepository reembolsoRepository;
    private final TransaccionPagoRepository transaccionRepository;
    private final UsuarioAppRepository usuarioRepository;
    private final PagosMapper pagosMapper;

    //  Se inyecta el Resolver, no la interfaz directa
    private final PasarelaGatewayResolver gatewayResolver;

    private final IAuditoriaService auditoriaService;

    @Override
    @Transactional
    public ReembolsoResponseDTO solicitarReembolso(SolicitarReembolsoRequestDTO request, String nombreUsuarioSolicitante) {
        TransaccionPago transaccion = transaccionRepository.findById(request.transaccionPagoId())
                .orElseThrow(() -> new BusinessRuleException("La transacción de pago no existe."));

        if (transaccion.getEstado() != EstadoTransaccion.COMPLETADO) {
            throw new BusinessRuleException("Solo se pueden reembolsar transacciones en estado COMPLETADO.");
        }
        if (request.monto().compareTo(transaccion.getMonto()) > 0) {
            throw new BusinessRuleException("El monto a reembolsar no puede ser mayor al monto original.");
        }

        UsuarioApp solicitante = usuarioRepository.findByNombreUsuario(nombreUsuarioSolicitante)
                .orElseThrow(() -> new BusinessRuleException("Usuario solicitante no encontrado."));

        Reembolso reembolso = Reembolso.builder()
                .transaccionPago(transaccion)
                .monto(request.monto())
                .motivo(request.motivo())
                .estado(EstadoReembolso.SOLICITADO)
                .build();

        return pagosMapper.toReembolsoResponse(reembolsoRepository.save(reembolso));
    }

    @Override
    @Transactional
    public ReembolsoResponseDTO aprobarReembolso(UUID reembolsoId, String nombreUsuarioAprobador) {
        Reembolso reembolso = reembolsoRepository.findById(reembolsoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reembolso no encontrado."));

        if (reembolso.getEstado() != EstadoReembolso.SOLICITADO) {
            throw new BusinessRuleException("Solo se pueden aprobar reembolsos en estado SOLICITADO.");
        }

        UsuarioApp aprobador = usuarioRepository.findByNombreUsuario(nombreUsuarioAprobador)
                .orElseThrow(() -> new BusinessRuleException("Usuario aprobador no encontrado."));

        TransaccionPago transaccion = reembolso.getTransaccionPago();

        IPasarelaPagoGateway gateway = gatewayResolver.resolver(transaccion.getProveedor());

        ResultadoGatewayDTO resultado = gateway.reembolsar(
                transaccion.getIdTransaccionProveedor(), reembolso.getMonto(), reembolso.getMotivo());

        if (!resultado.exitoso()) {
            reembolso.setEstado(EstadoReembolso.RECHAZADO);
            reembolsoRepository.save(reembolso);
            throw new BusinessRuleException("El proveedor rechazó el reembolso: " + resultado.motivoFallo());
        }

        reembolso.setEstado(EstadoReembolso.PROCESADO);
        reembolso.setAprobadoPor(aprobador);
        reembolso.setIdReembolsoProveedor(resultado.idTransaccionProveedor());
        reembolsoRepository.save(reembolso);

        transaccion.setEstado(EstadoTransaccion.REEMBOLSADO);
        transaccionRepository.save(transaccion);

        auditoriaService.registrarAccionInterna(
                aprobador.getUsuarioId(), "PAGOS", "REEMBOLSO_APROBADO", "Reembolso", reembolso.getReembolsoId(),
                null, Map.of("monto", reembolso.getMonto()), null, null);

        return pagosMapper.toReembolsoResponse(reembolso);
    }

    @Override
    @Transactional
    public ReembolsoResponseDTO rechazarReembolso(UUID reembolsoId, String motivo, String nombreUsuarioAprobador) {
        Reembolso reembolso = reembolsoRepository.findById(reembolsoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reembolso no encontrado."));

        if (reembolso.getEstado() != EstadoReembolso.SOLICITADO) {
            throw new BusinessRuleException("Solo se pueden rechazar reembolsos en estado SOLICITADO.");
        }

        reembolso.setEstado(EstadoReembolso.RECHAZADO);
        reembolso.setMotivo(reembolso.getMotivo() + " | Rechazado: " + motivo);
        return pagosMapper.toReembolsoResponse(reembolsoRepository.save(reembolso));
    }
}