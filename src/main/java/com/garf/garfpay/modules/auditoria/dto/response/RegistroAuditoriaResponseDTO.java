package com.garf.garfpay.modules.auditoria.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record RegistroAuditoriaResponseDTO(
        UUID auditoriaId,
        UUID usuarioId,
        String nombreModulo,
        String nombreAccion,
        String nombreEntidad,
        UUID idEntidad,
        Map<String, Object> valoresAnteriores,
        Map<String, Object> valoresNuevos,
        String direccionIp,
        String agenteUsuario,
        OffsetDateTime creadoEl
) {}