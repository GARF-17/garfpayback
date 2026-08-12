package com.garf.garfpay.modules.notificaciones.repository;

import com.garf.garfpay.modules.notificaciones.entity.EnvioWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EnvioWebhookRepository extends JpaRepository<EnvioWebhook, UUID> {
}