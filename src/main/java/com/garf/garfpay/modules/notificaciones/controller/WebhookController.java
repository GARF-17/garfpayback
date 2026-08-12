package com.garf.garfpay.modules.notificaciones.controller;

import com.garf.garfpay.modules.notificaciones.dto.request.CrearWebhookRequestDTO;
import com.garf.garfpay.modules.notificaciones.dto.request.RegistrarDispositivoRequestDTO;
import com.garf.garfpay.modules.notificaciones.dto.response.WebhookResponseDTO;
import com.garf.garfpay.modules.notificaciones.service.IWebhookService;
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
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class WebhookController {

    private final IWebhookService webhookService;

    @PostMapping("/organizaciones/{organizacionId}/webhooks")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<ApiResponse<WebhookResponseDTO>> configurarWebhook(
            @PathVariable UUID organizacionId,
            @Valid @RequestBody CrearWebhookRequestDTO request) {

        WebhookResponseDTO response = webhookService.configurarWebhook(organizacionId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Webhook configurado exitosamente", response));
    }

    @GetMapping("/organizaciones/{organizacionId}/webhooks")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<ApiResponse<List<WebhookResponseDTO>>> listarWebhooks(
            @PathVariable UUID organizacionId) {

        List<WebhookResponseDTO> response = webhookService.listarWebhooks(organizacionId);
        return ResponseEntity.ok(ApiResponse.success("Webhooks obtenidos", response));
    }

    @PostMapping("/usuarios/{usuarioId}/dispositivos")
    public ResponseEntity<ApiResponse<Void>> registrarDispositivoPush(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody RegistrarDispositivoRequestDTO request) {

        webhookService.registrarDispositivoPush(usuarioId, request);
        return ResponseEntity.ok(ApiResponse.success("Dispositivo registrado para recibir notificaciones", null));
    }
}