package com.garf.garfpay.modules.tenant.mapper;

import com.garf.garfpay.modules.tenant.dto.request.CrearCuentaLiquidacionRequestDTO;
import com.garf.garfpay.modules.tenant.dto.request.CrearOrganizacionRequestDTO;
import com.garf.garfpay.modules.tenant.dto.response.CuentaLiquidacionResponseDTO;
import com.garf.garfpay.modules.tenant.dto.response.MiembroOrganizacionResponseDTO;
import com.garf.garfpay.modules.tenant.dto.response.OrganizacionResponseDTO;
import com.garf.garfpay.modules.tenant.entity.CuentaLiquidacion;
import com.garf.garfpay.modules.tenant.entity.MiembroOrganizacion;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TenantMapper {

    // ORGANIZACION
    Organizacion toOrganizacionEntity(CrearOrganizacionRequestDTO request);
    OrganizacionResponseDTO toOrganizacionResponse(Organizacion entity);

    // CUENTA DE LIQUIDACION
    CuentaLiquidacion toCuentaLiquidacionEntity(CrearCuentaLiquidacionRequestDTO request);
    CuentaLiquidacionResponseDTO toCuentaLiquidacionResponse(CuentaLiquidacion entity);

    // MIEMBRO ORGANIZACION
    @Mapping(source = "usuario.nombreUsuario", target = "nombreUsuario")
    @Mapping(source = "rol.codigo", target = "rolCodigo")
    @Mapping(source = "rol.nombre", target = "rolNombre")
    @Mapping(source = "id.organizacionId", target = "organizacionId")
    @Mapping(source = "id.usuarioId", target = "usuarioId")
    MiembroOrganizacionResponseDTO toMiembroResponse(MiembroOrganizacion entity);
}