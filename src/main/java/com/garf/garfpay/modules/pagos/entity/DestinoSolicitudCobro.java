package com.garf.garfpay.modules.pagos.entity;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.pagos.enums.EstadoDestinoCobro;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "destino_solicitud_cobro", schema = "pagos")
public class DestinoSolicitudCobro {

    @EmbeddedId
    @Builder.Default
    private DestinoSolicitudCobroId id = new DestinoSolicitudCobroId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("solicitudCobroId")
    @JoinColumn(name = "solicitud_cobro_id", nullable = false)
    private SolicitudCobro solicitudCobro;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioApp usuario;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    @Column(nullable = false)
    private EstadoDestinoCobro estado = EstadoDestinoCobro.PENDIENTE;

    @Column(name = "monto_personalizado", precision = 18, scale = 2)
    private BigDecimal montoPersonalizado;

    @Column(name = "pagado_el")
    private OffsetDateTime pagadoEl;
}