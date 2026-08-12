package com.garf.garfpay.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración del cliente HTTP Reactivo (WebClient).
 * Se utilizará para hacer llamadas a APIs externas como pasarelas de pago
 * (Niubiz, Culqi), servicios de validación de identidad (KYC) o Bots de Telegram.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024)); // Permitir respuestas de hasta 2MB
    }
}