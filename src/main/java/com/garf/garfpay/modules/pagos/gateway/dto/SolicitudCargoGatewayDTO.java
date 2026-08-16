package com.garf.garfpay.modules.pagos.gateway.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record SolicitudCargoGatewayDTO(
        UUID transaccionPagoId,
        String claveIdempotencia,
        BigDecimal monto,
        String moneda,
        Map<String, Object> metadatos,
        String idCorrelacion
) {}