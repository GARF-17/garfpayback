package com.garf.garfpay.modules.facturacion.entity;

import com.garf.garfpay.modules.tenant.entity.Organizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "suscripcion_organizacion", schema = "facturacion")
public class SuscripcionOrganizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "suscripcion_organizacion_id", updatable = false, nullable = false)
    private UUID suscripcionOrganizacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_suscripcion_id", nullable = false)
    private PlanSuscripcion planSuscripcion;

    @Column(name = "inicia_el", nullable = false)
    private LocalDate iniciaEl;

    @Column(name = "termina_el", nullable = false)
    private LocalDate terminaEl;

    @Builder.Default
    @Column(name = "esta_activa")
    private Boolean estaActiva = true;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}