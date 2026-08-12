package com.garf.garfpay.modules.tenant.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MiembroOrganizacionResponseDTO(
        UUID organizacionId,
        UUID usuarioId,
        String nombreUsuario,
        String rolCodigo,
        String rolNombre,
        OffsetDateTime vinculadoEl
) {}