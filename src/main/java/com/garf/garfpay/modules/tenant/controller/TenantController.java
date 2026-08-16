package com.garf.garfpay.modules.tenant.controller;

// ... imports ...
import com.garf.garfpay.modules.tenant.dto.request.CambiarRolMiembroRequestDTO;
import com.garf.garfpay.modules.tenant.dto.request.CrearCuentaLiquidacionRequestDTO;
import com.garf.garfpay.modules.tenant.dto.request.CrearOrganizacionRequestDTO;
import com.garf.garfpay.modules.tenant.dto.response.CuentaLiquidacionResponseDTO;
import com.garf.garfpay.modules.tenant.dto.response.MiembroOrganizacionResponseDTO;
import com.garf.garfpay.modules.tenant.dto.response.OrganizacionResponseDTO;
import com.garf.garfpay.modules.tenant.service.ITenantService;
import com.garf.garfpay.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizaciones")
@RequiredArgsConstructor
public class TenantController {

    private final ITenantService tenantService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<OrganizacionResponseDTO>> crearOrganizacion(
            @Valid @RequestBody CrearOrganizacionRequestDTO request,
            Principal principal) {

        OrganizacionResponseDTO response = tenantService.crearOrganizacion(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Organización creada exitosamente", response));
    }

    @PostMapping("/{organizacionId}/cuentas-liquidacion")
    @PreAuthorize("@tenantGuard.tieneRolEnOrganizacion(#organizacionId, authentication, 'ORG_ADMIN', 'TREASURER') " +
            "or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CuentaLiquidacionResponseDTO>> agregarCuentaLiquidacion(
            @PathVariable UUID organizacionId,
            @Valid @RequestBody CrearCuentaLiquidacionRequestDTO request,
            Principal principal,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }

        CuentaLiquidacionResponseDTO response = tenantService.agregarCuentaLiquidacion(
                organizacionId, request, principal.getName(), ipAddress);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cuenta de liquidación agregada con éxito y auditada.", response));
    }

    @PatchMapping("/{organizacionId}/activar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<OrganizacionResponseDTO>> activarOrganizacion(
            @PathVariable UUID organizacionId) {

        OrganizacionResponseDTO response = tenantService.activarOrganizacion(organizacionId);

        return ResponseEntity.ok(ApiResponse.success("¡Organización verificada y activada con éxito!", response));
    }

    @PatchMapping("/{organizacionId}/miembros/{usuarioId}/rol")
    @PreAuthorize("@tenantGuard.tieneRolEnOrganizacion(#organizacionId, authentication, 'ORG_ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MiembroOrganizacionResponseDTO>> cambiarRolMiembro(
            @PathVariable UUID organizacionId,
            @PathVariable UUID usuarioId,
            @Valid @RequestBody CambiarRolMiembroRequestDTO request,
            Principal principal) {

        MiembroOrganizacionResponseDTO response = tenantService.cambiarRolMiembro(
                organizacionId, usuarioId, request, principal.getName());

        return ResponseEntity.ok(ApiResponse.success("Rol del miembro actualizado exitosamente", response));
    }
}