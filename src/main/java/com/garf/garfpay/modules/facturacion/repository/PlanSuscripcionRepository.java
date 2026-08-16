package com.garf.garfpay.modules.facturacion.repository;

import com.garf.garfpay.modules.facturacion.entity.PlanSuscripcion;
import com.garf.garfpay.modules.facturacion.enums.FrecuenciaSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanSuscripcionRepository extends JpaRepository<PlanSuscripcion, Long> {
    Optional<PlanSuscripcion> findByFrecuenciaAndEstaActivoTrue(FrecuenciaSuscripcion frecuencia);
    Optional<PlanSuscripcion> findByEsPlanPorDefectoTrueAndEstaActivoTrue();
}