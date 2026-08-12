package com.garf.garfpay.modules.cumplimiento.service;

import com.garf.garfpay.modules.cumplimiento.dto.request.IniciarKycRequestDTO;
import com.garf.garfpay.modules.cumplimiento.dto.response.VerificacionKycResponseDTO;
import com.garf.garfpay.modules.cumplimiento.enums.EstadoKyc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IKycService {
    VerificacionKycResponseDTO iniciarVerificacion(UUID organizacionId, IniciarKycRequestDTO request);
    VerificacionKycResponseDTO actualizarEstadoKyc(UUID verificacionKycId, EstadoKyc nuevoEstado, Map<String, Object> payloadRespuesta);
    List<VerificacionKycResponseDTO> listarVerificaciones(UUID organizacionId);
}