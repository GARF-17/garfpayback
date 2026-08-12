package com.garf.garfpay.modules.identidad.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegistroUsuarioResponseDTO(
        UUID usuarioId,
        String nombreUsuario,
        String correo,
        String estado,
        LocalDateTime creadoEl
) {}