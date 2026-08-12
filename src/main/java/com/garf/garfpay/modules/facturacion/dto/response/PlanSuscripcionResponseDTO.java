package com.garf.garfpay.modules.facturacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PlanSuscripcionResponseDTO(
        Long planSuscripcionId,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String frecuencia,
        Boolean estaActivo,
        OffsetDateTime creadoEl
) {}