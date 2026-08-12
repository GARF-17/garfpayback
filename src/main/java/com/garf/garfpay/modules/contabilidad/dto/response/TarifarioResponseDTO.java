package com.garf.garfpay.modules.contabilidad.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TarifarioResponseDTO(
        UUID tarifarioId,
        UUID organizacionId,
        String proveedor,
        BigDecimal comisionPorcentaje,
        BigDecimal comisionFija,
        OffsetDateTime vigenteDesde,
        OffsetDateTime vigenteHasta,
        OffsetDateTime creadoEl
) {}