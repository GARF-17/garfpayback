package com.garf.garfpay.modules.identidad.service;

import com.garf.garfpay.modules.identidad.dto.request.LoginRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.RegistroUsuarioRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.SolicitarCodigoRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.ValidarCodigoRequestDTO;
import com.garf.garfpay.modules.identidad.dto.response.LoginResponseDTO;
import com.garf.garfpay.modules.identidad.dto.response.RegistroUsuarioResponseDTO;

public interface IIdentidadService {
    RegistroUsuarioResponseDTO registrarUsuario(RegistroUsuarioRequestDTO request);
    LoginResponseDTO login(LoginRequestDTO request);
    void generarYEnviarCodigoVerificacion(SolicitarCodigoRequestDTO request);
    boolean validarCodigoVerificacion(ValidarCodigoRequestDTO request);
    LoginResponseDTO refrescarToken(String refreshToken);
}