package com.garf.garfpay.modules.auditoria.service;

import com.garf.garfpay.modules.auditoria.dto.response.RegistroAuditoriaResponseDTO;
import com.garf.garfpay.shared.response.PageResponse;

import java.util.Map;
import java.util.UUID;

public interface IAuditoriaService {

    // Método de uso INTERNO para otros módulos. NO se expone en el Controller.
    void registrarAccionInterna(
            UUID usuarioId, String nombreModulo, String nombreAccion,
            String nombreEntidad, UUID idEntidad,
            Map<String, Object> valoresAnteriores, Map<String, Object> valoresNuevos,
            String ip, String agenteUsuario);

    // Métodos de lectura para el Super Admin
    PageResponse<RegistroAuditoriaResponseDTO> listarAuditoriasPorEntidad(UUID idEntidad, int page, int size);
    PageResponse<RegistroAuditoriaResponseDTO> listarAuditoriasPorUsuario(UUID usuarioId, int page, int size);
}