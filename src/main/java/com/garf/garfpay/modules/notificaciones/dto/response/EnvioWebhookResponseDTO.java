package com.garf.garfpay.modules.notificaciones.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record EnvioWebhookResponseDTO(
        UUID webhookDeliveryId,
        String nombreEvento,
        Map<String, Object> payload,
        Integer codigoRespuesta,
        Boolean exitoso,
        Integer conteoReintentos,
        OffsetDateTime enviadoEl
) {}