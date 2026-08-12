package com.garf.garfpay.modules.identidad.dto.request;

import com.garf.garfpay.modules.identidad.enums.TipoVerificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ValidarCodigoRequestDTO(
        @NotNull(message = "El ID del usuario es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El tipo de verificación es obligatorio")
        TipoVerificacion tipo,

        @NotBlank(message = "El código de verificación no puede estar vacío")
        @Size(min = 6, max = 6, message = "El código debe tener exactamente 6 dígitos")
        String codigo
) {}