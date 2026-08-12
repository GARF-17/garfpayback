package com.garf.garfpay.modules.contabilidad.controller;

import com.garf.garfpay.modules.contabilidad.dto.response.LiquidacionResponseDTO;
import com.garf.garfpay.modules.contabilidad.service.ILiquidacionService;
import com.garf.garfpay.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contabilidad/liquidaciones")
@RequiredArgsConstructor
public class LiquidacionController {

    private final ILiquidacionService liquidacionService;

    @PostMapping("/organizaciones/{organizacionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TREASURER')")
    public ResponseEntity<ApiResponse<LiquidacionResponseDTO>> generarLiquidacion(
            @PathVariable UUID organizacionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        LiquidacionResponseDTO response = liquidacionService.generarLiquidacion(organizacionId, fechaInicio, fechaFin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Liquidación generada correctamente.", response));
    }

    @PatchMapping("/{liquidacionId}/confirmar")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TREASURER')")
    public ResponseEntity<ApiResponse<LiquidacionResponseDTO>> confirmarTransferencia(
            @PathVariable UUID liquidacionId,
            @RequestParam String referenciaTransferencia) {

        LiquidacionResponseDTO response = liquidacionService.confirmarTransferenciaBancaria(liquidacionId, referenciaTransferencia);
        return ResponseEntity.ok(ApiResponse.success("Transferencia bancaria confirmada exitosamente.", response));
    }

    @GetMapping("/organizaciones/{organizacionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN', 'TREASURER')")
    public ResponseEntity<ApiResponse<List<LiquidacionResponseDTO>>> listarPorOrganizacion(
            @PathVariable UUID organizacionId) {

        List<LiquidacionResponseDTO> response = liquidacionService.listarLiquidacionesPorOrganizacion(organizacionId);
        return ResponseEntity.ok(ApiResponse.success("Liquidaciones obtenidas correctamente.", response));
    }

    @GetMapping("/{liquidacionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN', 'TREASURER')")
    public ResponseEntity<ApiResponse<LiquidacionResponseDTO>> obtenerPorId(
            @PathVariable UUID liquidacionId) {

        LiquidacionResponseDTO response = liquidacionService.obtenerLiquidacionPorId(liquidacionId);
        return ResponseEntity.ok(ApiResponse.success("Detalle de liquidación obtenido correctamente.", response));
    }
}