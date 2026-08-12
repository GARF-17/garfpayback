package com.garf.garfpay.modules.pagos.entity;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.pagos.enums.EstadoReembolso;
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
@Table(name = "reembolso", schema = "pagos")
public class Reembolso {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reembolso_id", updatable = false, nullable = false)
    private UUID reembolsoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_pago_id", nullable = false)
    private TransaccionPago transaccionPago;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private EstadoReembolso estado = EstadoReembolso.SOLICITADO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private UsuarioApp aprobadoPor;

    @Column(name = "id_reembolso_proveedor", length = 150)
    private String idReembolsoProveedor;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;

    @UpdateTimestamp
    @Column(name = "actualizado_el")
    private OffsetDateTime actualizadoEl;
}