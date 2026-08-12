package com.garf.garfpay.modules.contabilidad.entity;

import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "liquidacion_detalle", schema = "contabilidad")
public class LiquidacionDetalle {

    @EmbeddedId
    @Builder.Default
    private LiquidacionDetalleId id = new LiquidacionDetalleId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("liquidacionId")
    @JoinColumn(name = "liquidacion_id", nullable = false)
    private Liquidacion liquidacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("transaccionPagoId")
    @JoinColumn(name = "transaccion_pago_id", nullable = false)
    private TransaccionPago transaccionPago;
}