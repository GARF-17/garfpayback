package com.garf.garfpay.modules.control_acceso.mapper;

import com.garf.garfpay.modules.control_acceso.dto.response.PermisoResponseDTO;
import com.garf.garfpay.modules.control_acceso.dto.response.RolResponseDTO;
import com.garf.garfpay.modules.control_acceso.entity.Permiso;
import com.garf.garfpay.modules.control_acceso.entity.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ControlAccesoMapper {

    PermisoResponseDTO toPermisoResponse(Permiso permiso);

    RolResponseDTO toRolResponse(Rol rol);
}