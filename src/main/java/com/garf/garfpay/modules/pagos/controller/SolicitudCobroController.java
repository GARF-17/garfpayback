package com.garf.garfpay.modules.pagos.controller;

import com.garf.garfpay.modules.pagos.dto.request.CrearSolicitudCobroRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.DeudaResponseDTO;
import com.garf.garfpay.modules.pagos.dto.response.SolicitudCobroResponseDTO;
import com.garf.garfpay.modules.pagos.service.ISolicitudCobroService;
import com.garf.garfpay.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class SolicitudCobroController {

    private final ISolicitudCobroService solicitudCobroService;

    @PostMapping("/organizaciones/{organizacionId}/solicitudes")
    @PreAuthorize("@tenantGuard.tieneRolEnOrganizacion(#organizacionId, authentication, 'ORG_ADMIN', 'TREASURER') " +
            "or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SolicitudCobroResponseDTO>> crearSolicitud(
            @PathVariable UUID organizacionId,
            @Valid @RequestBody CrearSolicitudCobroRequestDTO request,
            Authentication authentication) {

        SolicitudCobroResponseDTO response = solicitudCobroService.crearSolicitudCobro(organizacionId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Solicitud de cobro generada exitosamente", response));
    }

    @GetMapping("/usuarios/{usuarioId}/deudas")
    public ResponseEntity<ApiResponse<List<DeudaResponseDTO>>> listarDeudas(
            @PathVariable UUID usuarioId) {

        List<DeudaResponseDTO> response = solicitudCobroService.listarDeudasPorUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success("Deudas obtenidas correctamente", response));
    }
}