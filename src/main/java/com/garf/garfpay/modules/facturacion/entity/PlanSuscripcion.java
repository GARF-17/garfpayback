package com.garf.garfpay.modules.facturacion.entity;

import com.garf.garfpay.modules.facturacion.enums.FrecuenciaSuscripcion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "plan_suscripcion", schema = "facturacion")
public class PlanSuscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_suscripcion_id")
    private Long planSuscripcionId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private FrecuenciaSuscripcion frecuencia;

    @Builder.Default
    @Column(name = "es_plan_por_defecto")
    private Boolean esPlanPorDefecto = false;

    @Builder.Default
    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}