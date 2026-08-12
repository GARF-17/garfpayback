package com.garf.garfpay.modules.contabilidad.entity;

import com.garf.garfpay.modules.contabilidad.enums.EstadoLiquidacion;
import com.garf.garfpay.modules.tenant.entity.CuentaLiquidacion;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "liquidacion", schema = "contabilidad")
public class Liquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "liquidacion_id", updatable = false, nullable = false)
    private UUID liquidacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_liquidacion_id", nullable = false)
    private CuentaLiquidacion cuentaLiquidacion;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Builder.Default
    @Column(length = 10)
    private String moneda = "PEN";

    @Column(name = "monto_bruto", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoBruto;

    @Column(name = "monto_comisiones", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoComisiones;

    // Calculado por la BD (GENERATED ALWAYS AS STORED)
    @Column(name = "monto_neto", insertable = false, updatable = false)
    private BigDecimal montoNeto;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private EstadoLiquidacion estado = EstadoLiquidacion.PENDIENTE;

    @Column(name = "referencia_transferencia", length = 150)
    private String referenciaTransferencia;

    @Column(name = "liquidado_el")
    private OffsetDateTime liquidadoEl;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}