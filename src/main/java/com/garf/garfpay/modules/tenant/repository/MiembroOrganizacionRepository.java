package com.garf.garfpay.modules.tenant.repository;

import com.garf.garfpay.modules.tenant.entity.MiembroOrganizacion;
import com.garf.garfpay.modules.tenant.entity.MiembroOrganizacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MiembroOrganizacionRepository extends JpaRepository<MiembroOrganizacion, MiembroOrganizacionId> {
    @Query("SELECT m FROM MiembroOrganizacion m JOIN FETCH m.rol WHERE m.id.usuarioId = :usuarioId")
    List<MiembroOrganizacion> findByUsuarioId(@Param("usuarioId") UUID usuarioId);
}