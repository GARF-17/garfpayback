package com.garf.garfpay.modules.identidad.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegistroUsuarioResponseDTO(
        UUID usuarioId,
        String nombreUsuario,
        String correo,
        String estado,
        OffsetDateTime creadoEl
) {}