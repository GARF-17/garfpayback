package com.garf.garfpay.modules.cumplimiento.repository;

import com.garf.garfpay.modules.cumplimiento.entity.VerificacionKyc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VerificacionKycRepository extends JpaRepository<VerificacionKyc, UUID> {
    List<VerificacionKyc> findByOrganizacion_OrganizacionIdOrderByCreadoElDesc(UUID organizacionId);
}