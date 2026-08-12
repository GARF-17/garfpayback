package com.garf.garfpay.modules.control_acceso.entity;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "usuario_rol", schema = "control_acceso")
public class UsuarioRol {

    @EmbeddedId
    private UsuarioRolId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private UsuarioApp usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("rolId")
    @JoinColumn(name = "rol_id")
    private Rol rol;
}