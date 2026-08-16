package com.garf.garfpay.modules.notificaciones.repository;

import com.garf.garfpay.modules.notificaciones.entity.EnvioWebhook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EnvioWebhookRepository extends JpaRepository<EnvioWebhook, UUID> {
    List<EnvioWebhook> findByExitosoFalseAndConteoReintentosLessThanAndProximoReintentoElBefore(
            int maxReintentos, OffsetDateTime ahora, Pageable pageable);
}