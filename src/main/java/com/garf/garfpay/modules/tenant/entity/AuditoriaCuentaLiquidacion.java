package com.garf.garfpay.modules.tenant.entity;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "auditoria_cuenta_liquidacion", schema = "tenant")
public class AuditoriaCuentaLiquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "auditoria_id", updatable = false, nullable = false)
    private UUID auditoriaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_liquidacion_id", nullable = false)
    private CuentaLiquidacion cuentaLiquidacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cambiado_por", nullable = false)
    private UsuarioApp cambiadoPor;

    @Column(name = "cci_anterior", length = 50)
    private String cciAnterior;

    @Column(name = "cci_nuevo", length = 50)
    private String cciNuevo;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}