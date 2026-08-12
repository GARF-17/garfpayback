package com.garf.garfpay.modules.pagos.dto.request;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record ProcesarPagoRequestDTO(
        @NotNull(message = "El ID de la solicitud es obligatorio")
        UUID solicitudCobroId,

        @NotNull(message = "El proveedor es obligatorio")
        NombreProveedor proveedor,

        @NotBlank(message = "La clave de idempotencia es obligatoria para evitar cobros dobles")
        String claveIdempotencia,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        // Aquí viene el token de la tarjeta de Niubiz o Culqi
        Map<String, Object> metadatos
) {}