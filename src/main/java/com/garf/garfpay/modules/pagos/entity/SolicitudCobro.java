package com.garf.garfpay.modules.pagos.entity;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.pagos.enums.TipoSolicitudPago;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "solicitud_cobro", schema = "pagos")
public class SolicitudCobro {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "solicitud_cobro_id", updatable = false, nullable = false)
    private UUID solicitudCobroId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private TipoSolicitudPago tipo;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Builder.Default
    @Column(length = 10)
    private String moneda = "PEN";

    @Builder.Default
    @Column(name = "permite_pago_parcial")
    private Boolean permitePagoParcial = false;

    @Column(name = "expira_el")
    private OffsetDateTime expiraEl;

    @Builder.Default
    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private UsuarioApp creadoPor;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;

    @UpdateTimestamp
    @Column(name = "actualizado_el")
    private OffsetDateTime actualizadoEl;

    @Column(name = "eliminado_el")
    private OffsetDateTime eliminadoEl;
}