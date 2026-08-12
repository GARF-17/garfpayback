package com.garf.garfpay.modules.notificaciones.repository;

import com.garf.garfpay.modules.notificaciones.entity.PuntoEnlaceWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PuntoEnlaceWebhookRepository extends JpaRepository<PuntoEnlaceWebhook, UUID> {
    List<PuntoEnlaceWebhook> findByOrganizacion_OrganizacionId(UUID organizacionId);
}