package com.garf.garfpay.modules.pagos.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ReembolsoResponseDTO(
        UUID reembolsoId,
        UUID transaccionPagoId,
        BigDecimal monto,
        String motivo,
        String estado,
        OffsetDateTime creadoEl
) {}