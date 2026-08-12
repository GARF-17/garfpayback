package com.garf.garfpay.modules.tenant.repository;

import com.garf.garfpay.modules.tenant.entity.CuentaLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CuentaLiquidacionRepository extends JpaRepository<CuentaLiquidacion, UUID> {
    List<CuentaLiquidacion> findByOrganizacionOrganizacionIdAndEstaActivaTrue(UUID organizacionId);
    Optional<CuentaLiquidacion> findByOrganizacionOrganizacionIdAndEsPrincipalTrueAndEstaActivaTrue(UUID organizacionId);
}