package com.garf.garfpay.modules.pagos.repository;

import com.garf.garfpay.modules.pagos.entity.SolicitudCobro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SolicitudCobroRepository extends JpaRepository<SolicitudCobro, UUID> {
}