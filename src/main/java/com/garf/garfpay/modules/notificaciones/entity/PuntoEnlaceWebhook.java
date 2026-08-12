package com.garf.garfpay.modules.notificaciones.entity;

import com.garf.garfpay.modules.tenant.entity.Organizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "punto_enlace_webhook", schema = "notificaciones")
public class PuntoEnlaceWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "webhook_endpoint_id", updatable = false, nullable = false)
    private UUID webhookEndpointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @Column(name = "url_enlace", nullable = false, columnDefinition = "TEXT")
    private String urlEnlace;

    @Column(name = "clave_secreta", nullable = false, columnDefinition = "TEXT")
    private String claveSecreta;

    @Builder.Default
    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}