package com.garf.garfpay.modules.notificaciones.listener;

import com.garf.garfpay.modules.notificaciones.entity.EnvioWebhook;
import com.garf.garfpay.modules.notificaciones.entity.PuntoEnlaceWebhook;
import com.garf.garfpay.modules.notificaciones.event.TransaccionCompletadaEvent;
import com.garf.garfpay.modules.notificaciones.repository.EnvioWebhookRepository;
import com.garf.garfpay.modules.notificaciones.repository.PuntoEnlaceWebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * Escucha eventos de dominio (p.ej. pago completado) DESPUÉS de que la transacción
 * de base de datos haya hecho commit (AFTER_COMMIT), y dispara el webhook configurado
 * por la organización hacia su sistema externo, con firma HMAC-SHA256 para que el
 * receptor pueda verificar autenticidad.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookEventListener {

    private final PuntoEnlaceWebhookRepository puntoEnlaceRepository;
    private final EnvioWebhookRepository envioWebhookRepository;
    private final WebClient.Builder webClientBuilder;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alCompletarseTransaccion(TransaccionCompletadaEvent event) {
        List<PuntoEnlaceWebhook> endpoints = puntoEnlaceRepository
                .findByOrganizacion_OrganizacionId(event.getOrganizacionId())
                .stream()
                .filter(PuntoEnlaceWebhook::getEstaActivo)
                .toList();

        Map<String, Object> payload = Map.of(
                "evento", "transaccion.completada",
                "transaccionPagoId", event.getTransaccionPagoId().toString(),
                "organizacionId", event.getOrganizacionId().toString(),
                "timestamp", OffsetDateTime.now().toString());

        endpoints.forEach(endpoint -> enviar(endpoint, "transaccion.completada", payload));
    }

    public void enviar(PuntoEnlaceWebhook endpoint, String nombreEvento, Map<String, Object> payload) {
        String firma = firmarPayload(payload.toString(), endpoint.getClaveSecreta());

        EnvioWebhook envio = EnvioWebhook.builder()
                .endpoint(endpoint)
                .nombreEvento(nombreEvento)
                .payload(payload)
                .firma(firma)
                .conteoReintentos(0)
                .build();

        try {
            WebClient client = webClientBuilder.build();
            var respuesta = client.post()
                    .uri(endpoint.getUrlEnlace())
                    .header("X-GarfPay-Signature", firma)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toEntity(String.class)
                    .block(Duration.ofSeconds(10));

            envio.setCodigoRespuesta(respuesta != null ? respuesta.getStatusCode().value() : null);
            envio.setCuerpoRespuesta(respuesta != null ? respuesta.getBody() : null);
            envio.setExitoso(respuesta != null && respuesta.getStatusCode().is2xxSuccessful());

        } catch (WebClientResponseException httpEx) {
            envio.setCodigoRespuesta(httpEx.getStatusCode().value());
            envio.setCuerpoRespuesta(httpEx.getResponseBodyAsString());
            envio.setExitoso(false);
            envio.setProximoReintentoEl(OffsetDateTime.now().plusMinutes(5));
        } catch (Exception ex) {
            log.error("Fallo al entregar webhook a {}: {}", endpoint.getUrlEnlace(), ex.getMessage());
            envio.setExitoso(false);
            envio.setProximoReintentoEl(OffsetDateTime.now().plusMinutes(5));
        }

        envioWebhookRepository.save(envio);
    }

    private String firmarPayload(String payloadCrudo, String claveSecreta) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(claveSecreta.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payloadCrudo.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.error("No se pudo firmar el payload del webhook", e);
            return "";
        }
    }
}