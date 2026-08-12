package com.garf.garfpay.modules.pagos.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SolicitudCobroResponseDTO(
        UUID solicitudCobroId,
        String titulo,
        String descripcion,
        String tipo,
        BigDecimal monto,
        String moneda,
        Boolean permitePagoParcial,
        OffsetDateTime expiraEl,
        Boolean estaActivo,
        OffsetDateTime creadoEl
) {}