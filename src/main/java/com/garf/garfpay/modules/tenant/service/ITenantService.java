package com.garf.garfpay.modules.tenant.service;

import com.garf.garfpay.modules.tenant.dto.request.CambiarRolMiembroRequestDTO;
import com.garf.garfpay.modules.tenant.dto.request.CrearCuentaLiquidacionRequestDTO;
import com.garf.garfpay.modules.tenant.dto.request.CrearOrganizacionRequestDTO;
import com.garf.garfpay.modules.tenant.dto.response.CuentaLiquidacionResponseDTO;
import com.garf.garfpay.modules.tenant.dto.response.MiembroOrganizacionResponseDTO;
import com.garf.garfpay.modules.tenant.dto.response.OrganizacionResponseDTO;

import java.util.UUID;

public interface ITenantService {

    OrganizacionResponseDTO crearOrganizacion(CrearOrganizacionRequestDTO request, String nombreUsuarioCreador);
    CuentaLiquidacionResponseDTO agregarCuentaLiquidacion(UUID organizacionId, CrearCuentaLiquidacionRequestDTO request, String nombreUsuarioAuditor, String ipAddress);
    OrganizacionResponseDTO activarOrganizacion(UUID organizacionId);
    MiembroOrganizacionResponseDTO cambiarRolMiembro(UUID organizacionId, UUID usuarioId, CambiarRolMiembroRequestDTO request, String nombreUsuarioEjecutor);
}