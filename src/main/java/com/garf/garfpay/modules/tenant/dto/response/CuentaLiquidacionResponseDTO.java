package com.garf.garfpay.modules.tenant.dto.response;

import java.util.UUID;

public record CuentaLiquidacionResponseDTO(
        UUID cuentaLiquidacionId,
        String nombreBanco,
        String moneda,
        String numeroCuenta,
        String cci,
        String titularCuenta,
        String telefonoYape,
        Boolean esPrincipal,
        Boolean estaActiva
) {}