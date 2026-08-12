package com.garf.garfpay.modules.notificaciones.service;

import com.garf.garfpay.modules.notificaciones.dto.request.CrearWebhookRequestDTO;
import com.garf.garfpay.modules.notificaciones.dto.request.RegistrarDispositivoRequestDTO;
import com.garf.garfpay.modules.notificaciones.dto.response.WebhookResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IWebhookService {
    WebhookResponseDTO configurarWebhook(UUID organizacionId, CrearWebhookRequestDTO request);
    List<WebhookResponseDTO> listarWebhooks(UUID organizacionId);
    void registrarDispositivoPush(UUID usuarioId, RegistrarDispositivoRequestDTO request);
}