package com.garf.garfpay.modules.tenant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearCuentaLiquidacionRequestDTO(
        @NotBlank(message = "El nombre del banco es obligatorio")
        @Size(max = 100)
        String nombreBanco,

        @Size(max = 10)
        String moneda,

        @Size(max = 50)
        String numeroCuenta,

        @NotBlank(message = "El CCI es obligatorio para las transferencias")
        @Size(min = 20, max = 50, message = "El CCI debe tener al menos 20 caracteres")
        String cci,

        @Size(max = 150)
        String titularCuenta,

        @Size(max = 20)
        String telefonoYape
) {}