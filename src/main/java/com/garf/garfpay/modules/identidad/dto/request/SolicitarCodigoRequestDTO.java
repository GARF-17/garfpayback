package com.garf.garfpay.modules.identidad.dto.request;

import com.garf.garfpay.modules.identidad.enums.TipoVerificacion;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SolicitarCodigoRequestDTO(
        @NotNull(message = "El ID del usuario es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El tipo de verificación es obligatorio")
        TipoVerificacion tipo
) {}