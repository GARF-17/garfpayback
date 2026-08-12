package com.garf.garfpay.modules.cumplimiento.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record VerificacionKycResponseDTO(
        UUID verificacionKycId,
        UUID organizacionId,
        String nombreProveedor,
        Map<String, Object> payloadSolicitud,
        Map<String, Object> payloadRespuesta,
        String estado,
        OffsetDateTime creadoEl
) {}