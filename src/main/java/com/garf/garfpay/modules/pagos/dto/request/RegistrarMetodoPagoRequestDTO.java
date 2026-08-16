package com.garf.garfpay.modules.pagos.dto.request;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarMetodoPagoRequestDTO(
        @NotNull NombreProveedor proveedor,
        @NotBlank(message = "El token generado por el proveedor es obligatorio") String tokenProveedor,
        String marcaTarjeta,
        String ultimosCuatroDigitos
) {}