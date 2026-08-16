package com.garf.garfpay.modules.facturacion.controller;

import com.garf.garfpay.modules.facturacion.dto.request.CrearPlanRequestDTO;
import com.garf.garfpay.modules.facturacion.dto.request.SuscribirOrganizacionRequestDTO;
import com.garf.garfpay.modules.facturacion.dto.response.PlanSuscripcionResponseDTO;
import com.garf.garfpay.modules.facturacion.dto.response.SuscripcionOrganizacionResponseDTO;
import com.garf.garfpay.modules.facturacion.service.IFacturacionService;
import com.garf.garfpay.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/facturacion")
@RequiredArgsConstructor
public class FacturacionController {

    private final IFacturacionService facturacionService;

    @PostMapping("/planes")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PlanSuscripcionResponseDTO>> crearPlanSuscripcion(
            @Valid @RequestBody CrearPlanRequestDTO request) {
        PlanSuscripcionResponseDTO response = facturacionService.crearPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Plan de suscripción creado exitosamente", response));
    }

    @GetMapping("/planes")
    public ResponseEntity<ApiResponse<List<PlanSuscripcionResponseDTO>>> listarPlanesDisponibles() {
        List<PlanSuscripcionResponseDTO> response = facturacionService.listarPlanesActivos();

        return ResponseEntity.ok(ApiResponse.success("Planes obtenidos correctamente", response));
    }

    @PostMapping("/suscripciones/manual")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SuscripcionOrganizacionResponseDTO>> asignarSuscripcionManual(
            @Valid @RequestBody SuscribirOrganizacionRequestDTO request) {
        SuscripcionOrganizacionResponseDTO response = facturacionService.asignarSuscripcionManual(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Suscripción manual asignada a la organización", response));
    }

    // ENDPOINTS PARA EL CLIENTE (ORG_ADMIN / TESORERO)
    @GetMapping("/organizaciones/{organizacionId}/suscripcion-activa")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN', 'TREASURER')")
    public ResponseEntity<ApiResponse<SuscripcionOrganizacionResponseDTO>> obtenerSuscripcionActiva(
            @PathVariable UUID organizacionId) {

        SuscripcionOrganizacionResponseDTO response = facturacionService.obtenerSuscripcionActiva(organizacionId);
        return ResponseEntity.ok(ApiResponse.success("Detalle de suscripción obtenido", response));
    }
}