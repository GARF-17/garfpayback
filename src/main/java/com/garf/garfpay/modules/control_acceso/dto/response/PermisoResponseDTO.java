package com.garf.garfpay.modules.control_acceso.dto.response;

public record PermisoResponseDTO(
        Long permisoId,
        String codigo,
        String descripcion
) {}