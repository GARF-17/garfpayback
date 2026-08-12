package com.garf.garfpay.modules.contabilidad.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record EventoTransaccionResponseDTO(
        UUID eventoTransaccionId,
        UUID transaccionPagoId,
        String codigoEvento,
        String descripcion,
        Map<String, Object> payload,
        OffsetDateTime creadoEl
) {}