package com.garf.garfpay.modules.pagos.controller;

import com.garf.garfpay.modules.pagos.dto.request.SolicitarReembolsoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.ReembolsoResponseDTO;
import com.garf.garfpay.modules.pagos.service.IReembolsoService;
import com.garf.garfpay.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class ReembolsoController {

    private final IReembolsoService reembolsoService;

    @PostMapping("/reembolsos")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN', 'TREASURER')")
    public ResponseEntity<ApiResponse<ReembolsoResponseDTO>> solicitarReembolso(
            @Valid @RequestBody SolicitarReembolsoRequestDTO request,
            Authentication authentication) {

        ReembolsoResponseDTO response = reembolsoService.solicitarReembolso(request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reembolso procesado exitosamente", response));
    }
}