package com.garf.garfpay.modules.tenant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MiembroOrganizacionId implements Serializable {

    @Column(name = "organizacion_id")
    private UUID organizacionId;

    @Column(name = "usuario_id")
    private UUID usuarioId;
}