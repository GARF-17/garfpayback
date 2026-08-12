package com.garf.garfpay.modules.contabilidad.repository;

import com.garf.garfpay.modules.contabilidad.entity.Liquidacion;
import com.garf.garfpay.modules.contabilidad.enums.EstadoLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LiquidacionRepository extends JpaRepository<Liquidacion, UUID> {
    List<Liquidacion> findByOrganizacion_OrganizacionId(UUID organizacionId);
    List<Liquidacion> findByEstado(EstadoLiquidacion estado);
}