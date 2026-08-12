package com.garf.garfpay.modules.notificaciones.service.impl;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.notificaciones.dto.request.CrearWebhookRequestDTO;
import com.garf.garfpay.modules.notificaciones.dto.request.RegistrarDispositivoRequestDTO;
import com.garf.garfpay.modules.notificaciones.dto.response.WebhookResponseDTO;
import com.garf.garfpay.modules.notificaciones.entity.DispositivoUsuario;
import com.garf.garfpay.modules.notificaciones.entity.PuntoEnlaceWebhook;
import com.garf.garfpay.modules.notificaciones.mapper.WebhookMapper;
import com.garf.garfpay.modules.notificaciones.repository.DispositivoUsuarioRepository;
import com.garf.garfpay.modules.notificaciones.repository.PuntoEnlaceWebhookRepository;
import com.garf.garfpay.modules.notificaciones.service.IWebhookService;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements IWebhookService {

    private final PuntoEnlaceWebhookRepository webhookRepository;
    private final DispositivoUsuarioRepository dispositivoRepository;
    private final OrganizacionRepository organizacionRepository;
    private final UsuarioAppRepository usuarioRepository;
    private final WebhookMapper webhookMapper;

    @Override
    @Transactional
    public WebhookResponseDTO configurarWebhook(UUID organizacionId, CrearWebhookRequestDTO request) {
        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada"));

        PuntoEnlaceWebhook webhook = webhookMapper.toWebhookEntity(request);
        webhook.setOrganizacion(organizacion);

        // Generar clave criptográfica si el cliente no envía una
        if (request.claveSecreta() == null || request.claveSecreta().isBlank()) {
            String claveGenerada = "whsec_" + UUID.randomUUID().toString().replace("-", "");
            webhook.setClaveSecreta(claveGenerada);
        }

        webhook = webhookRepository.save(webhook);
        log.info("Webhook creado para la organización {}", organizacionId);

        return webhookMapper.toWebhookResponse(webhook);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookResponseDTO> listarWebhooks(UUID organizacionId) {
        return webhookRepository.findByOrganizacion_OrganizacionId(organizacionId).stream()
                .map(webhookMapper::toWebhookResponse)
                .toList();
    }

    @Override
    @Transactional
    public void registrarDispositivoPush(UUID usuarioId, RegistrarDispositivoRequestDTO request) {
        UsuarioApp usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Evita fallos de UNIQUE CONSTRAINT si el usuario se reloguea.
        Optional<DispositivoUsuario> dispositivoExistente =
                dispositivoRepository.findByUsuario_UsuarioIdAndTokenPush(usuarioId, request.tokenPush());

        if (dispositivoExistente.isPresent()) {
            DispositivoUsuario dispositivo = dispositivoExistente.get();
            dispositivo.setUltimoUsoEl(OffsetDateTime.now());
            dispositivo.setEstaActivo(true);
            dispositivoRepository.save(dispositivo);
            log.info("Token Push actualizado para el usuario {}", usuarioId);
        } else {
            DispositivoUsuario nuevoDispositivo = DispositivoUsuario.builder()
                    .usuario(usuario)
                    .tokenPush(request.tokenPush())
                    .plataforma(request.plataforma())
                    .ultimoUsoEl(OffsetDateTime.now())
                    .build();
            dispositivoRepository.save(nuevoDispositivo);
            log.info("Nuevo Token Push registrado para el usuario {}", usuarioId);
        }
    }
}