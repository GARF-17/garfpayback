package com.garf.garfpay.modules.notificaciones.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record CrearWebhookRequestDTO(
        @NotBlank(message = "La URL del webhook es obligatoria")
        @URL(message = "Debe ser una URL válida (Ej: https://colegio.com/api/webhooks)")
        String urlEnlace,

        String claveSecreta
) {}