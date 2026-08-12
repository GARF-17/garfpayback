package com.garf.garfpay.modules.pagos.repository;

import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, UUID> {
    boolean existsByClaveIdempotencia(String claveIdempotencia);

    // Trae solo transacciones COMPLETADAS que NO existen en ninguna liquidación.
    @Query("SELECT t FROM TransaccionPago t " +
            "WHERE t.solicitudCobro.organizacion.organizacionId = :organizacionId " +
            "AND t.estado = 'COMPLETADO' " +
            "AND t.creadoEl >= :fechaInicio AND t.creadoEl <= :fechaFin " +
            "AND t.transaccionPagoId NOT IN (SELECT ld.transaccionPago.transaccionPagoId FROM LiquidacionDetalle ld)")
    List<TransaccionPago> buscarTransaccionesNoLiquidadas(
            @Param("organizacionId") UUID organizacionId,
            @Param("fechaInicio") OffsetDateTime fechaInicio,
            @Param("fechaFin") OffsetDateTime fechaFin);
}