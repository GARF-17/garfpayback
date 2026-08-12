package com.garf.garfpay.modules.contabilidad.dto.request;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CrearTarifarioRequestDTO(
        UUID organizacionId,

        @NotNull(message = "El proveedor adquirente es obligatorio")
        NombreProveedor proveedor,

        @NotNull(message = "El porcentaje de comisión es obligatorio")
        @DecimalMin(value = "0.0", message = "La comisión en porcentaje no puede ser negativa")
        BigDecimal comisionPorcentaje,

        @NotNull(message = "La comisión fija es obligatoria")
        @DecimalMin(value = "0.0", message = "La comisión fija no puede ser negativa")
        BigDecimal comisionFija,

        @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
        OffsetDateTime vigenteDesde,

        OffsetDateTime vigenteHasta
) {}