package com.garf.garfpay.modules.identidad.mapper;

import com.garf.garfpay.modules.identidad.dto.request.RegistroUsuarioRequestDTO;
import com.garf.garfpay.modules.identidad.dto.response.RegistroUsuarioResponseDTO;
import com.garf.garfpay.modules.identidad.entity.PerfilUsuario;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    PerfilUsuario toPerfilUsuario(RegistroUsuarioRequestDTO requestDTO);
    @Mapping(target = "claveHash", ignore = true)
    UsuarioApp toUsuarioApp(RegistroUsuarioRequestDTO requestDTO);
    @Mapping(source = "perfil.correo", target = "correo")
    RegistroUsuarioResponseDTO toRegistroResponse(UsuarioApp usuarioApp);
}