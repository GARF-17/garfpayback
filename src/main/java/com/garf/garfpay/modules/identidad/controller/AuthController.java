package com.garf.garfpay.modules.identidad.controller;

import com.garf.garfpay.modules.identidad.dto.request.LoginRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.RegistroUsuarioRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.SolicitarCodigoRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.ValidarCodigoRequestDTO;
import com.garf.garfpay.modules.identidad.dto.response.LoginResponseDTO;
import com.garf.garfpay.modules.identidad.dto.response.RegistroUsuarioResponseDTO;
import com.garf.garfpay.modules.identidad.service.IIdentidadService;
import com.garf.garfpay.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IIdentidadService identidadService;

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<RegistroUsuarioResponseDTO>> registrarUsuario(
            @Valid @RequestBody RegistroUsuarioRequestDTO request) {
        RegistroUsuarioResponseDTO response = identidadService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario registrado exitosamente", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = identidadService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Inicio de sesión exitoso", response));
    }

    // --- NUEVOS ENDPOINTS DE VERIFICACIÓN OTP ---

    @PostMapping("/codigo/solicitar")
    public ResponseEntity<ApiResponse<Void>> solicitarCodigo(
            @Valid @RequestBody SolicitarCodigoRequestDTO request) {

        identidadService.generarYEnviarCodigoVerificacion(request);

        return ResponseEntity.ok(ApiResponse.success("Código enviado correctamente", null));
    }

    @PostMapping("/codigo/validar")
    public ResponseEntity<ApiResponse<Boolean>> validarCodigo(
            @Valid @RequestBody ValidarCodigoRequestDTO request) {

        identidadService.validarCodigoVerificacion(request);

        return ResponseEntity.ok(ApiResponse.success("Código verificado con éxito", true));
    }
}