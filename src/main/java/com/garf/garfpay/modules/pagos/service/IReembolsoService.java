package com.garf.garfpay.modules.pagos.service;

import com.garf.garfpay.modules.pagos.dto.request.SolicitarReembolsoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.ReembolsoResponseDTO;

import java.util.UUID;

public interface IReembolsoService {
    ReembolsoResponseDTO solicitarReembolso(SolicitarReembolsoRequestDTO request, String nombreUsuarioSolicitante);
    ReembolsoResponseDTO aprobarReembolso(UUID reembolsoId, String nombreUsuarioAprobador);
    ReembolsoResponseDTO rechazarReembolso(UUID reembolsoId, String motivo, String nombreUsuarioAprobador);
}