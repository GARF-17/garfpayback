package com.garf.garfpay.modules.pagos.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransaccionResponseDTO(
        UUID transaccionPagoId,
        UUID solicitudCobroId,
        String proveedor,
        String estado,
        BigDecimal monto,
        BigDecimal montoNeto,
        String moneda,
        String referenciaProveedor,
        OffsetDateTime creadoEl
) {}