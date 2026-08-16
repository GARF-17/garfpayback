package com.garf.garfpay.modules.tenant.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record HistorialRolMiembroResponseDTO(
        UUID historialRolMiembroId,
        UUID organizacionId,
        UUID usuarioId,
        String rolAnteriorCodigo,
        String rolNuevoCodigo,
        UUID cambiadoPorUsuarioId,
        OffsetDateTime creadoEl
) {}