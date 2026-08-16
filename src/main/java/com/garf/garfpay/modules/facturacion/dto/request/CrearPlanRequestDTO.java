package com.garf.garfpay.modules.facturacion.dto.request;

import com.garf.garfpay.modules.facturacion.enums.FrecuenciaSuscripcion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CrearPlanRequestDTO(
        @NotBlank String nombre,
        String descripcion,
        @NotNull @Positive BigDecimal precio,
        @NotNull FrecuenciaSuscripcion frecuencia,
        boolean esPlanPorDefecto
) {}
