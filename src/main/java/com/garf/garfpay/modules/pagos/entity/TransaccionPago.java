package com.garf.garfpay.modules.pagos.entity;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.pagos.enums.EstadoTransaccion;
import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.tenant.entity.CuentaLiquidacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "transaccion_pago", schema = "pagos")
public class TransaccionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaccion_pago_id", updatable = false, nullable = false)
    private UUID transaccionPagoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_cobro_id")
    private SolicitudCobro solicitudCobro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_pagador_id")
    private UsuarioApp usuarioPagador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_liquidacion_id")
    private CuentaLiquidacion cuentaLiquidacion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private NombreProveedor proveedor;

    @Column(name = "id_transaccion_proveedor", length = 150)
    private String idTransaccionProveedor;

    @Column(name = "referencia_proveedor", length = 150)
    private String referenciaProveedor;

    @Column(name = "referencia_transaccion", length = 150)
    private String referenciaTransaccion;

    @Column(name = "id_traza", length = 150)
    private String idTraza;

    @Column(name = "id_correlacion", length = 150)
    private String idCorrelacion;

    @Column(name = "clave_idempotencia", length = 150, unique = true, nullable = false)
    private String claveIdempotencia;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Builder.Default
    @Column(name = "comision_pasarela", precision = 18, scale = 4)
    private BigDecimal comisionPasarela = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "comision_plataforma", precision = 18, scale = 4)
    private BigDecimal comisionPlataforma = BigDecimal.ZERO;

    @Column(name = "monto_neto", insertable = false, updatable = false)
    private BigDecimal montoNeto;

    @Builder.Default
    @Column(length = 10)
    private String moneda = "PEN";

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private EstadoTransaccion estado = EstadoTransaccion.PENDIENTE;

    @Column(name = "motivo_fallo", columnDefinition = "TEXT")
    private String motivoFallo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadatos;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "respuesta_proveedor", columnDefinition = "jsonb")
    private Map<String, Object> respuestaProveedor;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;

    @UpdateTimestamp
    @Column(name = "actualizado_el")
    private OffsetDateTime actualizadoEl;
}