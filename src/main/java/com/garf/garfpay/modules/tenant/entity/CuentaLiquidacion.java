package com.garf.garfpay.modules.tenant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "cuenta_liquidacion", schema = "tenant")
public class CuentaLiquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cuenta_liquidacion_id", updatable = false, nullable = false)
    private UUID cuentaLiquidacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @Column(name = "nombre_banco", nullable = false, length = 100)
    private String nombreBanco;

    @Builder.Default
    @Column(length = 10)
    private String moneda = "PEN";

    @Column(name = "numero_cuenta", length = 50)
    private String numeroCuenta;

    @Column(nullable = false, length = 50)
    private String cci;

    @Column(name = "titular_cuenta", length = 150)
    private String titularCuenta;

    @Column(name = "telefono_yape", length = 20)
    private String telefonoYape;

    @Builder.Default
    @Column(name = "es_principal")
    private Boolean esPrincipal = true;

    @Builder.Default
    @Column(name = "esta_activa")
    private Boolean estaActiva = true;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;

    @UpdateTimestamp
    @Column(name = "actualizado_el")
    private OffsetDateTime actualizadoEl;

    @Column(name = "eliminado_el")
    private OffsetDateTime eliminadoEl;
}