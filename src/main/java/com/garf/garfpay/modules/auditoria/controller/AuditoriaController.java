package com.garf.garfpay.modules.auditoria.controller;

import com.garf.garfpay.modules.auditoria.dto.response.RegistroAuditoriaResponseDTO;
import com.garf.garfpay.modules.auditoria.service.IAuditoriaService;
import com.garf.garfpay.shared.response.ApiResponse;
import com.garf.garfpay.shared.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final IAuditoriaService auditoriaService;

    // Ver todo lo que ha pasado con un registro específico
    @GetMapping("/entidades/{idEntidad}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<RegistroAuditoriaResponseDTO>>> obtenerPorEntidad(
            @PathVariable UUID idEntidad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<RegistroAuditoriaResponseDTO> response = auditoriaService.listarAuditoriasPorEntidad(idEntidad, page, size);
        return ResponseEntity.ok(ApiResponse.success("Historial de la entidad obtenido exitosamente", response));
    }

    // Ver todo lo que ha hecho un usuario específico en el sistema
    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<RegistroAuditoriaResponseDTO>>> obtenerPorUsuario(
            @PathVariable UUID usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<RegistroAuditoriaResponseDTO> response = auditoriaService.listarAuditoriasPorUsuario(usuarioId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Historial de actividad del usuario obtenido exitosamente", response));
    }
}