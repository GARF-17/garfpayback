package com.garf.garfpay.modules.contabilidad.entity;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "tarifario", schema = "contabilidad")
public class Tarifario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tarifario_id", updatable = false, nullable = false)
    private UUID tarifarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id")
    private Organizacion organizacion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private NombreProveedor proveedor;

    @Builder.Default
    @Column(name = "comision_porcentaje", precision = 5, scale = 4, nullable = false)
    private BigDecimal comisionPorcentaje = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "comision_fija", precision = 18, scale = 2, nullable = false)
    private BigDecimal comisionFija = BigDecimal.ZERO;

    @Column(name = "vigente_desde", nullable = false)
    private OffsetDateTime vigenteDesde;

    @Column(name = "vigente_hasta")
    private OffsetDateTime vigenteHasta;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}