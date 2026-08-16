package com.garf.garfpay.modules.pagos.service.impl;

import com.garf.garfpay.modules.pagos.entity.EventoProveedor;
import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import com.garf.garfpay.modules.pagos.enums.EstadoTransaccion;
import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.repository.EventoProveedorRepository;
import com.garf.garfpay.modules.pagos.repository.TransaccionPagoRepository;
import com.garf.garfpay.modules.pagos.service.IEventoProveedorService;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventoProveedorServiceImpl implements IEventoProveedorService {

    private final EventoProveedorRepository eventoProveedorRepository;
    private final TransaccionPagoRepository transaccionPagoRepository;

    @Override
    @Transactional
    public void procesarEventoEntrante(NombreProveedor proveedor, String firma, Map<String, Object> payload) {

        String idExterno = String.valueOf(payload.get("id"));
        String estadoProveedor = String.valueOf(payload.get("status"));
        String idTransaccionProveedor = String.valueOf(payload.getOrDefault("charge_id", payload.get("id")));

        // Idempotencia de eventos: la BD tiene UNIQUE(proveedor, id_externo_evento_proveedor)
        EventoProveedor evento = EventoProveedor.builder()
                .proveedor(proveedor)
                .idExternoEventoProveedor(idExterno)
                .estadoProveedor(estadoProveedor)
                .payload(payload)
                .build();

        Optional<TransaccionPago> transaccionOpt = transaccionPagoRepository
                .findByProveedorAndIdTransaccionProveedor(proveedor, idTransaccionProveedor);

        if (transaccionOpt.isEmpty()) {
            log.warn("Evento de proveedor {} recibido para una transacción no reconocida (idProveedor={})",
                    proveedor, idTransaccionProveedor);
            throw new BusinessRuleException("Transacción no encontrada para el evento recibido.");
        }

        TransaccionPago transaccion = transaccionOpt.get();
        evento.setTransaccionPago(transaccion);
        eventoProveedorRepository.save(evento);

        // Reconciliación de estado según el evento del PSP
        aplicarTransicionDeEstado(transaccion, estadoProveedor);
        transaccionPagoRepository.save(transaccion);
    }

    private void aplicarTransicionDeEstado(TransaccionPago transaccion, String estadoProveedor) {
        switch (estadoProveedor.toLowerCase()) {
            case "paid", "captured" -> transaccion.setEstado(EstadoTransaccion.COMPLETADO);
            case "failed", "declined" -> transaccion.setEstado(EstadoTransaccion.FALLIDO);
            case "refunded" -> transaccion.setEstado(EstadoTransaccion.REEMBOLSADO);
            default -> log.info("Estado de proveedor '{}' no mapeado; se conserva el estado actual.", estadoProveedor);
        }
    }
}