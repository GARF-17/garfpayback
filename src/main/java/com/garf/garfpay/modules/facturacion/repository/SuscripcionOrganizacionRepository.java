package com.garf.garfpay.modules.facturacion.repository;

import com.garf.garfpay.modules.facturacion.entity.SuscripcionOrganizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SuscripcionOrganizacionRepository extends JpaRepository<SuscripcionOrganizacion, UUID> {

    // Busca todas las suscripciones activas cuya fecha de término sea HOY o antes de HOY
    @Query("SELECT s FROM SuscripcionOrganizacion s JOIN FETCH s.planSuscripcion JOIN FETCH s.organizacion WHERE s.estaActiva = true AND s.terminaEl <= :hoy")
    List<SuscripcionOrganizacion> buscarSuscripcionesPorVencer(@Param("hoy") LocalDate hoy);

    @Query("SELECT s FROM SuscripcionOrganizacion s JOIN FETCH s.planSuscripcion WHERE s.organizacion.organizacionId = :organizacionId AND s.estaActiva = true")
    Optional<SuscripcionOrganizacion> buscarSuscripcionActivaPorOrganizacion(@Param("organizacionId") UUID organizacionId);}