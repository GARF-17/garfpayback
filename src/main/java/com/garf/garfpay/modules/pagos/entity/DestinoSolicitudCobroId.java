package com.garf.garfpay.modules.pagos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class DestinoSolicitudCobroId implements Serializable {

    @Column(name = "solicitud_cobro_id")
    private UUID solicitudCobroId;

    @Column(name = "usuario_id")
    private UUID usuarioId;
}