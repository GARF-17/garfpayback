package com.garf.garfpay.modules.tenant.dto.request;

import jakarta.validation.constraints.NotNull;

public record CambiarRolMiembroRequestDTO(
        @NotNull(message = "El ID del nuevo rol es obligatorio")
        Long rolNuevoId
) {}