package com.garf.garfpay.modules.tenant.entity;

import com.garf.garfpay.modules.control_acceso.entity.Rol;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "historial_rol_miembro", schema = "tenant")
public class HistorialRolMiembro {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "historial_rol_miembro_id", updatable = false, nullable = false)
    private UUID historialRolMiembroId;

    // Mapeo de la llave foránea compuesta hacia miembro_organizacion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "organizacion_id", referencedColumnName = "organizacion_id", nullable = false),
            @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id", nullable = false)
    })
    private MiembroOrganizacion miembro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_anterior_id")
    private Rol rolAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_nuevo_id", nullable = false)
    private Rol rolNuevo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cambiado_por")
    private UsuarioApp cambiadoPor;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}