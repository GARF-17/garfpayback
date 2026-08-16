package com.garf.garfpay.modules.notificaciones.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class TransaccionCompletadaEvent extends ApplicationEvent {
    private final UUID transaccionPagoId;
    private final UUID organizacionId;

    public TransaccionCompletadaEvent(Object source, UUID transaccionPagoId, UUID organizacionId) {
        super(source);
        this.transaccionPagoId = transaccionPagoId;
        this.organizacionId = organizacionId;
    }

    public UUID getTransaccionPagoId() { return transaccionPagoId; }
    public UUID getOrganizacionId() { return organizacionId; }
}