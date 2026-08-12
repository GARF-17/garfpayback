package com.garf.garfpay.modules.pagos.entity;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "evento_proveedor", schema = "pagos")
public class EventoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "evento_proveedor_id", updatable = false, nullable = false)
    private UUID eventoProveedorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_pago_id", nullable = false)
    private TransaccionPago transaccionPago;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private NombreProveedor proveedor;

    @Column(name = "id_externo_evento_proveedor", length = 150)
    private String idExternoEventoProveedor;

    @Column(name = "estado_proveedor", length = 100)
    private String estadoProveedor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @CreationTimestamp
    @Column(name = "recibido_el", updatable = false)
    private OffsetDateTime recibidoEl;
}