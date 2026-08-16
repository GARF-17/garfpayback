package com.garf.garfpay.modules.notificaciones.job;

import com.garf.garfpay.modules.notificaciones.entity.EnvioWebhook;
import com.garf.garfpay.modules.notificaciones.listener.WebhookEventListener;
import com.garf.garfpay.modules.notificaciones.repository.EnvioWebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookRetryJob {

    private static final int MAX_REINTENTOS = 5;

    private final EnvioWebhookRepository envioWebhookRepository;
    private final WebhookEventListener webhookEventListener;

    @Scheduled(fixedDelay = 60_000) // cada minuto
    public void reintentarEntregasFallidas() {
        List<EnvioWebhook> pendientes = envioWebhookRepository
                .findByExitosoFalseAndConteoReintentosLessThanAndProximoReintentoElBefore(
                        MAX_REINTENTOS, OffsetDateTime.now(), PageRequest.of(0, 50));

        pendientes.forEach(envio -> {
            log.info("Reintentando webhook {} (intento {})", envio.getWebhookDeliveryId(), envio.getConteoReintentos() + 1);
            envio.setConteoReintentos(envio.getConteoReintentos() + 1);
            webhookEventListener.enviar(envio.getEndpoint(), envio.getNombreEvento(), envio.getPayload());
        });
    }
}