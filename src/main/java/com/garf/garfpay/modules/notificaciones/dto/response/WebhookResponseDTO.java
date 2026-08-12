package com.garf.garfpay.modules.notificaciones.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WebhookResponseDTO(
        UUID webhookEndpointId,
        UUID organizacionId,
        String urlEnlace,
        String claveSecreta,
        Boolean estaActivo,
        OffsetDateTime creadoEl
) {}