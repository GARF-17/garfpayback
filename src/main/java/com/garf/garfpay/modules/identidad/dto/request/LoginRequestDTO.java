package com.garf.garfpay.modules.identidad.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El nombre de usuario o correo es obligatorio")
        String nombreUsuario,

        @NotBlank(message = "La contraseña es obligatoria")
        String clave
) {}