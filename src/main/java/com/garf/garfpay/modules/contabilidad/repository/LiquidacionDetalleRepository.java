package com.garf.garfpay.modules.contabilidad.repository;

import com.garf.garfpay.modules.contabilidad.entity.LiquidacionDetalle;
import com.garf.garfpay.modules.contabilidad.entity.LiquidacionDetalleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LiquidacionDetalleRepository extends JpaRepository<LiquidacionDetalle, LiquidacionDetalleId> {
    List<LiquidacionDetalle> findByLiquidacion_LiquidacionId(UUID liquidacionId);
}