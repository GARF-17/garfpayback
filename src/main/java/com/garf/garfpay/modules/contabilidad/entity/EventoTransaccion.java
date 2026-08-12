package com.garf.garfpay.modules.contabilidad.entity;

import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "evento_transaccion", schema = "contabilidad")
public class EventoTransaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "evento_transaccion_id", updatable = false, nullable = false)
    private UUID eventoTransaccionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_pago_id", nullable = false)
    private TransaccionPago transaccionPago;

    @Column(name = "codigo_evento", length = 100, nullable = false)
    private String codigoEvento;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}