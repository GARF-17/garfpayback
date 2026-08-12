package com.garf.garfpay.modules.pagos.service.impl;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.pagos.dto.request.SolicitarReembolsoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.ReembolsoResponseDTO;
import com.garf.garfpay.modules.pagos.entity.Reembolso;
import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import com.garf.garfpay.modules.pagos.enums.EstadoReembolso;
import com.garf.garfpay.modules.pagos.enums.EstadoTransaccion;
import com.garf.garfpay.modules.pagos.mapper.PagosMapper;
import com.garf.garfpay.modules.pagos.repository.ReembolsoRepository;
import com.garf.garfpay.modules.pagos.repository.TransaccionPagoRepository;
import com.garf.garfpay.modules.pagos.service.IReembolsoService;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReembolsoServiceImpl implements IReembolsoService {

    private final ReembolsoRepository reembolsoRepository;
    private final TransaccionPagoRepository transaccionRepository;
    private final UsuarioAppRepository usuarioRepository;
    private final PagosMapper pagosMapper;

    @Override
    @Transactional
    public ReembolsoResponseDTO solicitarReembolso(SolicitarReembolsoRequestDTO request, String nombreUsuarioSolicitante) {

        // Validar que la transacción original existe
        TransaccionPago transaccion = transaccionRepository.findById(request.transaccionPagoId())
                .orElseThrow(() -> new BusinessRuleException("La transacción de pago no existe."));

        // Solo reembolsar cobros exitosos
        if (transaccion.getEstado() != EstadoTransaccion.COMPLETADO) {
            throw new BusinessRuleException("Solo se pueden reembolsar transacciones que estén en estado COMPLETADO.");
        }

        // El monto no puede ser mayor al pagado originalmente
        if (request.monto().compareTo(transaccion.getMonto()) > 0) {
            throw new BusinessRuleException("El monto a reembolsar no puede ser mayor al monto original de la transacción.");
        }

        // Buscar al administrador o tesorero que está aprobando esto
        UsuarioApp usuarioAprobador = usuarioRepository.findByNombreUsuario(nombreUsuarioSolicitante)
                .orElseThrow(() -> new BusinessRuleException("Usuario solicitante no encontrado."));

        // ====================================================================
        // AQUÍ IRÍA LA LLAMADA A LA API DE NIUBIZ/CULQI PARA DEVOLVER EL DINERO
        // boolean reembolsoExitoso = pasarelaClient.reembolsar(transaccion.getIdTransaccionProveedor(), request.monto());
        // ====================================================================

        // 5. Crear el registro en la tabla de Reembolsos
        Reembolso reembolso = Reembolso.builder()
                .transaccionPago(transaccion)
                .monto(request.monto())
                .motivo(request.motivo())
                .estado(EstadoReembolso.APROBADO)
                .aprobadoPor(usuarioAprobador)
                .idReembolsoProveedor("REFUND-" + UUID.randomUUID().toString().substring(0, 8))
                .build();

        reembolso = reembolsoRepository.save(reembolso);

        // Actualizar el estado de la transacción original para que nadie la vuelva a reembolsar
        transaccion.setEstado(EstadoTransaccion.REEMBOLSADO);
        transaccionRepository.save(transaccion);

        return pagosMapper.toReembolsoResponse(reembolso);
    }
}