package com.garf.garfpay.modules.pagos.controller;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.service.IEventoProveedorService;
import com.garf.garfpay.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoint público (whitelisteado en SecurityConfig bajo /api/v1/webhooks/**)
 * que recibe las notificaciones asíncronas de cada PSP sobre el resultado real
 * de una operación (confirmaciones, contracargos, reembolsos procesados, etc.).
 */
@RestController
@RequestMapping("/api/v1/webhooks/proveedores")
@RequiredArgsConstructor
public class EventoProveedorWebhookController {

    private final IEventoProveedorService eventoProveedorService;

    @PostMapping("/{proveedor}")
    public ResponseEntity<ApiResponse<Void>> recibirEvento(
            @PathVariable NombreProveedor proveedor,
            @RequestHeader(value = "X-Signature", required = false) String firma,
            @RequestBody Map<String, Object> payload) {

        eventoProveedorService.procesarEventoEntrante(proveedor, firma, payload);
        return ResponseEntity.ok(ApiResponse.success("Evento recibido", null));
    }
}