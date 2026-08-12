package com.garf.garfpay.modules.control_acceso.dto.response;

import com.garf.garfpay.modules.control_acceso.enums.AmbitoRol;
import java.util.Set;

public record RolResponseDTO(
        Long rolId,
        String codigo,
        String nombre,
        String descripcion,
        AmbitoRol ambito,
        Boolean esSistema,
        Set<PermisoResponseDTO> permisos
) {}