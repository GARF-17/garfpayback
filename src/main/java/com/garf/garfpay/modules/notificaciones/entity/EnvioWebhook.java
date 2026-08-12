package com.garf.garfpay.modules.notificaciones.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "envio_webhook", schema = "notificaciones")
public class EnvioWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "webhook_delivery_id", updatable = false, nullable = false)
    private UUID webhookDeliveryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webhook_endpoint_id", nullable = false)
    private PuntoEnlaceWebhook endpoint;

    @Column(name = "nombre_evento", length = 100, nullable = false)
    private String nombreEvento;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(columnDefinition = "TEXT")
    private String firma;

    @Column(name = "codigo_respuesta")
    private Integer codigoRespuesta;

    @Column(name = "cuerpo_respuesta", columnDefinition = "TEXT")
    private String cuerpoRespuesta;

    private Boolean exitoso;

    @Builder.Default
    @Column(name = "conteo_reintentos")
    private Integer conteoReintentos = 0;

    @Column(name = "proximo_reintento_el")
    private OffsetDateTime proximoReintentoEl;

    @CreationTimestamp
    @Column(name = "enviado_el", updatable = false)
    private OffsetDateTime enviadoEl;
}