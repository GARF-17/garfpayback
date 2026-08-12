package com.garf.garfpay.modules.control_acceso.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRolId implements Serializable {

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "rol_id")
    private Long rolId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioRolId that = (UsuarioRolId) o;
        return Objects.equals(usuarioId, that.usuarioId) && Objects.equals(rolId, that.rolId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, rolId);
    }
}