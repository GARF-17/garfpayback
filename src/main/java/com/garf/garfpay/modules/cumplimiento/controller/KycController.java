package com.garf.garfpay.modules.cumplimiento.controller;

import com.garf.garfpay.modules.cumplimiento.dto.request.IniciarKycRequestDTO;
import com.garf.garfpay.modules.cumplimiento.dto.response.VerificacionKycResponseDTO;
import com.garf.garfpay.modules.cumplimiento.enums.EstadoKyc;
import com.garf.garfpay.modules.cumplimiento.service.IKycService;
import com.garf.garfpay.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cumplimiento")
@RequiredArgsConstructor
public class KycController {

    private final IKycService kycService;

    // El colegio envía sus documentos
    @PostMapping("/organizaciones/{organizacionId}/verificaciones")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<ApiResponse<VerificacionKycResponseDTO>> iniciarKyc(
            @PathVariable UUID organizacionId,
            @Valid @RequestBody IniciarKycRequestDTO request) {

        VerificacionKycResponseDTO response = kycService.iniciarVerificacion(organizacionId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Proceso de validación KYC iniciado", response));
    }

    // El analista de GarfPay (o un Webhook) aprueba/rechaza el KYC
    @PatchMapping("/verificaciones/{verificacionKycId}/estado")
    @PreAuthorize("hasRole('SUPER_ADMIN')") // Solo GarfPay puede aprobar cuentas
    public ResponseEntity<ApiResponse<VerificacionKycResponseDTO>> actualizarEstadoKyc(
            @PathVariable UUID verificacionKycId,
            @RequestParam EstadoKyc estado,
            @RequestBody(required = false) Map<String, Object> payloadRespuesta) {

        VerificacionKycResponseDTO response = kycService.actualizarEstadoKyc(verificacionKycId, estado, payloadRespuesta);
        return ResponseEntity.ok(ApiResponse.success("Estado de KYC actualizado exitosamente", response));
    }

    // Ver el historial de verificaciones
    @GetMapping("/organizaciones/{organizacionId}/verificaciones")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<ApiResponse<List<VerificacionKycResponseDTO>>> listarVerificaciones(
            @PathVariable UUID organizacionId) {

        List<VerificacionKycResponseDTO> response = kycService.listarVerificaciones(organizacionId);
        return ResponseEntity.ok(ApiResponse.success("Historial KYC obtenido", response));
    }
}