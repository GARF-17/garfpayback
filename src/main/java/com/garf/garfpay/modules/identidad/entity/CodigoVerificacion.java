package com.garf.garfpay.modules.identidad.entity;

import com.garf.garfpay.modules.identidad.enums.TipoVerificacion;
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
@Table(name = "codigo_verificacion", schema = "identidad")
public class CodigoVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "codigo_verificacion_id", updatable = false, nullable = false)
    private UUID codigoVerificacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioApp usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoVerificacion tipo;

    @Column(name = "codigo_hash", nullable = false, length = 255)
    private String codigoHash;

    @Builder.Default
    @Column(name = "intentos")
    private Integer intentos = 0;

    @Column(name = "expira_el", nullable = false)
    private LocalDateTime expiraEl;

    @Column(name = "usado_el")
    private LocalDateTime usadoEl;

    @CreatedDate
    @Column(name = "creado_el", updatable = false)
    private LocalDateTime creadoEl;
}