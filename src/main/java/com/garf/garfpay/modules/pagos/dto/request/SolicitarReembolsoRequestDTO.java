package com.garf.garfpay.modules.pagos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record SolicitarReembolsoRequestDTO(
        @NotNull(message = "El ID de la transacción es obligatorio")
        UUID transaccionPagoId,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotBlank(message = "Debe especificar un motivo para el reembolso")
        String motivo
) {}