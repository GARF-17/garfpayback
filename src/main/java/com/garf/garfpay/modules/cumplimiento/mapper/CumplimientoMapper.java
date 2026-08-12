package com.garf.garfpay.modules.cumplimiento.mapper;

import com.garf.garfpay.modules.cumplimiento.dto.response.VerificacionKycResponseDTO;
import com.garf.garfpay.modules.cumplimiento.entity.VerificacionKyc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CumplimientoMapper {

    @Mapping(source = "organizacion.organizacionId", target = "organizacionId")
    VerificacionKycResponseDTO toResponse(VerificacionKyc entity);
}