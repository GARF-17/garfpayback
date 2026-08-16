package com.garf.garfpay.modules.identidad.entity;

import com.garf.garfpay.modules.identidad.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "perfil_usuario", schema = "identidad")
public class PerfilUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "perfil_id", updatable = false, nullable = false)
    private UUID perfilId;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", unique = true, nullable = false, length = 30)
    private String numeroDocumento;

    @Column(name = "correo", unique = true, nullable = false, length = 120)
    private String correo;

    @Column(name = "telefono", unique = true, length = 20)
    private String telefono;

    @Builder.Default
    @Column(name = "correo_verificado")
    private Boolean correoVerificado = false;

    @Builder.Default
    @Column(name = "telefono_verificado")
    private Boolean telefonoVerificado = false;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "url_avatar", columnDefinition = "TEXT")
    private String urlAvatar;

    @CreatedDate
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
}