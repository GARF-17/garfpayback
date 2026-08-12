package com.garf.garfpay.modules.tenant.repository;

import com.garf.garfpay.modules.tenant.entity.AuditoriaCuentaLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditoriaCuentaLiquidacionRepository extends JpaRepository<AuditoriaCuentaLiquidacion, UUID> {
}