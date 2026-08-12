package com.garf.garfpay.modules.auditoria.mapper;

import com.garf.garfpay.modules.auditoria.dto.response.RegistroAuditoriaResponseDTO;
import com.garf.garfpay.modules.auditoria.entity.RegistroAuditoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditoriaMapper {

    @Mapping(source = "usuario.usuarioId", target = "usuarioId")
    RegistroAuditoriaResponseDTO toResponse(RegistroAuditoria entity);
}