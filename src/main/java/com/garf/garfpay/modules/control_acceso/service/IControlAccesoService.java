package com.garf.garfpay.modules.control_acceso.service;

import com.garf.garfpay.modules.control_acceso.dto.request.CrearRolRequestDTO;
import com.garf.garfpay.modules.control_acceso.dto.response.PermisoResponseDTO;
import com.garf.garfpay.modules.control_acceso.dto.response.RolResponseDTO;

import java.util.List;

public interface IControlAccesoService {
    RolResponseDTO crearRol(CrearRolRequestDTO request);
    List<RolResponseDTO> listarRoles();
    List<PermisoResponseDTO> listarPermisos();
}