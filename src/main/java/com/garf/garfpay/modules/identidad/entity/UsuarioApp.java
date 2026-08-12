package com.garf.garfpay.modules.identidad.entity;

import com.garf.garfpay.modules.control_acceso.entity.UsuarioRol;
import com.garf.garfpay.modules.identidad.enums.EstadoUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@EntityListeners(AuditingEntityListener.class)
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
    private LocalDateTime ultimoLoginEl;

    @Builder.Default
    @Column(name = "mfa_habilitado")
    private Boolean mfaHabilitado = false;

    @Column(name = "mfa_secreto")
    private String mfaSecreto;

    @CreatedDate
    @Column(name = "creado_el", updatable = false)
    private LocalDateTime creadoEl;

    @Column(name = "actualizado_el")
    private LocalDateTime actualizadoEl;

    @Column(name = "eliminado_el")
    private LocalDateTime eliminadoEl;

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEl = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UsuarioRol> roles = new HashSet<>();
}