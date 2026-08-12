package com.garf.garfpay.modules.cumplimiento.entity;

import com.garf.garfpay.modules.cumplimiento.enums.EstadoKyc;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "verificacion_kyc", schema = "cumplimiento")
public class VerificacionKyc {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "verificacion_kyc_id", updatable = false, nullable = false)
    private UUID verificacionKycId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @Column(name = "nombre_proveedor", length = 100)
    private String nombreProveedor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_solicitud", columnDefinition = "jsonb")
    private Map<String, Object> payloadSolicitud;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_respuesta", columnDefinition = "jsonb")
    private Map<String, Object> payloadRespuesta;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    @Column(nullable = false)
    private EstadoKyc estado = EstadoKyc.PENDIENTE;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}