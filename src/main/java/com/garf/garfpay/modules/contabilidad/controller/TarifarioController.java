package com.garf.garfpay.modules.contabilidad.controller;

import com.garf.garfpay.modules.contabilidad.dto.request.CrearTarifarioRequestDTO;
import com.garf.garfpay.modules.contabilidad.dto.response.TarifarioResponseDTO;
import com.garf.garfpay.modules.contabilidad.service.ITarifarioService;
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
@RequestMapping("/api/v1/contabilidad/tarifarios")
@RequiredArgsConstructor
public class TarifarioController {

    private final ITarifarioService tarifarioService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TarifarioResponseDTO>> crearTarifario(
            @Valid @RequestBody CrearTarifarioRequestDTO request) {

        TarifarioResponseDTO response = tarifarioService.crearTarifario(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tarifario creado exitosamente.", response));
    }

    @GetMapping("/organizaciones/{organizacionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN', 'TREASURER')")
    public ResponseEntity<ApiResponse<List<TarifarioResponseDTO>>> listarTarifarios(
            @PathVariable UUID organizacionId) {

        List<TarifarioResponseDTO> response = tarifarioService.listarTarifariosPorOrganizacion(organizacionId);
        return ResponseEntity.ok(ApiResponse.success("Tarifarios obtenidos correctamente.", response));
    }
}