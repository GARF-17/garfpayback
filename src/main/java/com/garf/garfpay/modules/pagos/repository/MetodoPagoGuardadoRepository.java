package com.garf.garfpay.modules.pagos.repository;

import com.garf.garfpay.modules.pagos.entity.MetodoPagoGuardado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetodoPagoGuardadoRepository extends JpaRepository<MetodoPagoGuardado, UUID> {
    Optional<MetodoPagoGuardado> findByOrganizacion_OrganizacionIdAndEsPredeterminadoTrueAndEstaActivoTrue(UUID organizacionId);
    List<MetodoPagoGuardado> findByOrganizacion_OrganizacionIdAndEstaActivoTrue(UUID organizacionId);
}