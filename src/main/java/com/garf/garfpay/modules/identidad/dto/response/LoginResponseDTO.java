package com.garf.garfpay.modules.identidad.dto.response;

import java.util.UUID;

public record LoginResponseDTO(
        String token,
        String refreshToken,
        UUID usuarioId,
        String nombres,
        String apellidos,
        String correo,
        String rol
) {}