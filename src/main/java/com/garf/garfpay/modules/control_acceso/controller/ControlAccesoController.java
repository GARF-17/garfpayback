package com.garf.garfpay.modules.control_acceso.controller;

import com.garf.garfpay.modules.control_acceso.dto.request.CrearRolRequestDTO;
import com.garf.garfpay.modules.control_acceso.dto.response.PermisoResponseDTO;
import com.garf.garfpay.modules.control_acceso.dto.response.RolResponseDTO;
import com.garf.garfpay.modules.control_acceso.service.IControlAccesoService;
import com.garf.garfpay.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/control-acceso")
@RequiredArgsConstructor
public class ControlAccesoController {

    private final IControlAccesoService controlAccesoService;

    @PostMapping("/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RolResponseDTO>> crearRol(
            @Valid @RequestBody CrearRolRequestDTO request) {
        RolResponseDTO response = controlAccesoService.crearRol(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rol creado exitosamente", response));
    }
    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<ApiResponse<List<RolResponseDTO>>> listarRoles() {
        List<RolResponseDTO> response = controlAccesoService.listarRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles obtenidos correctamente", response));
    }

    @GetMapping("/permisos")
    public ResponseEntity<ApiResponse<List<PermisoResponseDTO>>> listarPermisos() {
        List<PermisoResponseDTO> response = controlAccesoService.listarPermisos();
        return ResponseEntity.ok(ApiResponse.success("Permisos obtenidos correctamente", response));
    }
}