package com.garf.garfpay.modules.pagos.controller;

import com.garf.garfpay.modules.pagos.dto.request.RegistrarMetodoPagoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.MetodoPagoResponseDTO;
import com.garf.garfpay.modules.pagos.service.IMetodoPagoService;
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
@RequestMapping("/api/v1/pagos/organizaciones/{organizacionId}/metodos-pago")
@RequiredArgsConstructor
public class MetodoPagoController {

    private final IMetodoPagoService metodoPagoService;

    @PostMapping
    @PreAuthorize("@tenantGuard.tieneRolEnOrganizacion(#organizacionId, authentication, 'ORG_ADMIN', 'TREASURER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MetodoPagoResponseDTO>> registrar(
            @PathVariable UUID organizacionId,
            @Valid @RequestBody RegistrarMetodoPagoRequestDTO request) {
        MetodoPagoResponseDTO response = metodoPagoService.registrarMetodoPago(organizacionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Método de pago registrado", response));
    }

    @GetMapping
    @PreAuthorize("@tenantGuard.tieneRolEnOrganizacion(#organizacionId, authentication, 'ORG_ADMIN', 'TREASURER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<MetodoPagoResponseDTO>>> listar(@PathVariable UUID organizacionId) {
        return ResponseEntity.ok(ApiResponse.success("Métodos de pago obtenidos", metodoPagoService.listarMetodosPago(organizacionId)));
    }

    @DeleteMapping("/{metodoPagoId}")
    @PreAuthorize("@tenantGuard.tieneRolEnOrganizacion(#organizacionId, authentication, 'ORG_ADMIN', 'TREASURER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID organizacionId, @PathVariable UUID metodoPagoId) {
        metodoPagoService.eliminarMetodoPago(organizacionId, metodoPagoId);
        return ResponseEntity.ok(ApiResponse.success("Método de pago eliminado", null));
    }
}