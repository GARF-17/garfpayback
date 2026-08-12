package com.garf.garfpay.modules.contabilidad.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LiquidacionResponseDTO(
        UUID liquidacionId,
        UUID organizacionId,
        String bancoDestino,
        String cciDestino,
        String titularCuenta,
        LocalDate periodoInicio,
        LocalDate periodoFin,
        String moneda,
        BigDecimal montoBruto,
        BigDecimal montoComisiones,
        BigDecimal montoNeto,
        String estado,
        String referenciaTransferencia,
        OffsetDateTime liquidadoEl,
        OffsetDateTime creadoEl
) {}