package com.garf.garfpay.modules.notificaciones.event;

import org.springframework.context.ApplicationEvent;
import java.util.UUID;

public class SuscripcionSuspendidaEvent extends ApplicationEvent {
    private final UUID suscripcionId;
    private final UUID organizacionId;
    private final String motivo; // <-- Nuevo campo

    public SuscripcionSuspendidaEvent(Object source, UUID suscripcionId, UUID organizacionId, String motivo) {
        super(source);
        this.suscripcionId = suscripcionId;
        this.organizacionId = organizacionId;
        this.motivo = motivo; // <-- Asignación
    }

    public UUID getSuscripcionId() { return suscripcionId; }
    public UUID getOrganizacionId() { return organizacionId; }
    public String getMotivo() { return motivo; } // <-- Getter
}