package com.garf.garfpay.modules.pagos.controller;

import com.garf.garfpay.modules.pagos.dto.request.ProcesarPagoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.TransaccionResponseDTO;
import com.garf.garfpay.modules.pagos.service.ITransaccionPagoService;
import com.garf.garfpay.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class TransaccionPagoController {

    private final ITransaccionPagoService transaccionPagoService;

    @PostMapping("/usuarios/{usuarioId}/transacciones")
    public ResponseEntity<ApiResponse<TransaccionResponseDTO>> procesarPago(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody ProcesarPagoRequestDTO request) {

        TransaccionResponseDTO response = transaccionPagoService.procesarPago(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pago procesado exitosamente", response));
    }
}