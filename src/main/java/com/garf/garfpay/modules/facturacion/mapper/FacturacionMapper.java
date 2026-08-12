package com.garf.garfpay.modules.facturacion.mapper;

import com.garf.garfpay.modules.facturacion.dto.response.PlanSuscripcionResponseDTO;
import com.garf.garfpay.modules.facturacion.dto.response.SuscripcionOrganizacionResponseDTO;
import com.garf.garfpay.modules.facturacion.entity.PlanSuscripcion;
import com.garf.garfpay.modules.facturacion.entity.SuscripcionOrganizacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FacturacionMapper {

    @Mapping(source = "organizacion.organizacionId", target = "organizacionId")
    @Mapping(source = "planSuscripcion.nombre", target = "nombrePlan")
    @Mapping(source = "planSuscripcion.precio", target = "precioPlan")
    SuscripcionOrganizacionResponseDTO toSuscripcionResponse(SuscripcionOrganizacion entity);

    PlanSuscripcionResponseDTO toPlanResponse(PlanSuscripcion entity);
}