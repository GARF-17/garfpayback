package com.garf.garfpay.modules.identidad.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sesion_usuario", schema = "identidad")
public class SesionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sesion_id", updatable = false, nullable = false)
    private UUID sesionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioApp usuario;

    @Column(name = "hash_token_refresco", columnDefinition = "TEXT")
    private String hashTokenRefresco;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "nombre_dispositivo", length = 150)
    private String nombreDispositivo;

    @Column(name = "agente_usuario", columnDefinition = "TEXT")
    private String agenteUsuario;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Builder.Default
    @Column(name = "esta_activa")
    private Boolean estaActiva = true;

    @CreatedDate
    @Column(name = "login_el", updatable = false)
    private LocalDateTime loginEl;

    @Column(name = "logout_el")
    private LocalDateTime logoutEl;
}