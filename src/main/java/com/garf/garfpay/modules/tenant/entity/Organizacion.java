package com.garf.garfpay.modules.tenant.entity;

import com.garf.garfpay.modules.tenant.enums.CategoriaOrganizacion;
import com.garf.garfpay.modules.tenant.enums.EstadoOrganizacion;
import com.garf.garfpay.modules.tenant.enums.TipoOrganizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "organizacion", schema = "tenant")
public class Organizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "organizacion_id", updatable = false, nullable = false)
    private UUID organizacionId;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_organizacion", nullable = false)
    private TipoOrganizacion tipoOrganizacion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "categoria", nullable = false)
    private CategoriaOrganizacion categoria;

    @Column(name = "documento_identidad", length = 20)
    private String documentoIdentidad;

    @Column(length = 120)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "url_logo", columnDefinition = "TEXT")
    private String urlLogo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    @Column(nullable = false)
    private EstadoOrganizacion estado = EstadoOrganizacion.ACTIVA;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;

    @UpdateTimestamp
    @Column(name = "actualizado_el")
    private OffsetDateTime actualizadoEl;

    @Column(name = "eliminado_el")
    private OffsetDateTime eliminadoEl;
}