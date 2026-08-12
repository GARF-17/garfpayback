package com.garf.garfpay.modules.cumplimiento.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record IniciarKycRequestDTO(
        @NotBlank(message = "El nombre del proveedor de KYC es obligatorio")
        String nombreProveedor,

        @NotNull(message = "Debe enviar la información (payload) para validar a la organización")
        Map<String, Object> payloadSolicitud
) {}