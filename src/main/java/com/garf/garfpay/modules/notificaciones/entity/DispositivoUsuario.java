package com.garf.garfpay.modules.notificaciones.entity;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.notificaciones.enums.Plataforma;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "dispositivo_usuario", schema = "notificaciones")
public class DispositivoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "dispositivo_id", updatable = false, nullable = false)
    private UUID dispositivoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioApp usuario;

    @Column(name = "token_push", nullable = false, columnDefinition = "TEXT")
    private String tokenPush;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Plataforma plataforma;

    @Builder.Default
    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "ultimo_uso_el")
    private OffsetDateTime ultimoUsoEl;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}