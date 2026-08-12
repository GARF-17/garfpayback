package com.garf.garfpay.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita la auditoría de JPA.
 * Permite usar anotaciones como @CreatedDate y @LastModifiedDate en las entidades
 * para que Spring Boot asigne las fechas automáticamente al insertar o actualizar.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Más adelante, aquí también podemos configurar un "AuditorAware" para
    // saber qué usuario (UUID) hizo el cambio automáticamente, leyendo el JWT.
}