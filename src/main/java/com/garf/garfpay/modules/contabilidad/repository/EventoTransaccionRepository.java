package com.garf.garfpay.modules.contabilidad.repository;

import com.garf.garfpay.modules.contabilidad.entity.EventoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventoTransaccionRepository extends JpaRepository<EventoTransaccion, UUID> {
    List<EventoTransaccion> findByTransaccionPago_TransaccionPagoId(UUID transaccionPagoId);
}