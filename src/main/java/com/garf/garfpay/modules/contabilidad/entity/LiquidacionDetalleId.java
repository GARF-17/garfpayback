package com.garf.garfpay.modules.contabilidad.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode @Embeddable
public class LiquidacionDetalleId implements Serializable {
    @Column(name = "liquidacion_id")
    private UUID liquidacionId;

    @Column(name = "transaccion_pago_id")
    private UUID transaccionPagoId;
}