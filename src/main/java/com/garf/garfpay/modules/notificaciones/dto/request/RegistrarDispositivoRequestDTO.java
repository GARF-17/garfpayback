package com.garf.garfpay.modules.notificaciones.dto.request;

import com.garf.garfpay.modules.notificaciones.enums.Plataforma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarDispositivoRequestDTO(
        @NotBlank(message = "El token FCM/APNS no puede estar vacío")
        String tokenPush,

        @NotNull(message = "La plataforma es obligatoria (IOS, ANDROID)")
        Plataforma plataforma
) {}