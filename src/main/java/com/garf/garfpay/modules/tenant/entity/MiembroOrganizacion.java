package com.garf.garfpay.modules.tenant.entity;

import com.garf.garfpay.modules.control_acceso.entity.Rol;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "miembro_organizacion", schema = "tenant")
public class MiembroOrganizacion {

    @EmbeddedId
    @Builder.Default
    private MiembroOrganizacionId id = new MiembroOrganizacionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("organizacionId")
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioApp usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @CreationTimestamp
    @Column(name = "vinculado_el", updatable = false)
    private OffsetDateTime vinculadoEl;
}