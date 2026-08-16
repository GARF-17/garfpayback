package com.garf.garfpay.modules.identidad.entity;

import com.garf.garfpay.modules.control_acceso.entity.UsuarioRol;
import com.garf.garfpay.modules.identidad.enums.EstadoUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "usuario_app", schema = "identidad")
public class UsuarioApp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usuario_id", updatable = false, nullable = false)
    private UUID usuarioId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "perfil_id", referencedColumnName = "perfil_id", nullable = false)
    private PerfilUsuario perfil;

    @Column(name = "nombre_usuario", unique = true, nullable = false, length = 50)
    private String nombreUsuario;

    @Column(name = "clave_hash", nullable = false)
    private String claveHash;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "estado")
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Builder.Default
    @Column(name = "intentos_fallidos_login")
    private Integer intentosFallidosLogin = 0;

    @Column(name = "ultimo_login_el")
    private OffsetDateTime ultimoLoginEl;

    @Builder.Default
    @Column(name = "mfa_habilitado")
    private Boolean mfaHabilitado = false;

    @Column(name = "mfa_secreto")
    private String mfaSecreto;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;

    @Column(name = "actualizado_el")
    private OffsetDateTime actualizadoEl;

    @Column(name = "eliminado_el")
    private OffsetDateTime eliminadoEl;

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEl = OffsetDateTime.now();
    }

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UsuarioRol> roles = new HashSet<>();
}