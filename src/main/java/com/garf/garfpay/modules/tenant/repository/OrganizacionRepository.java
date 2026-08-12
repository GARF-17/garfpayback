package com.garf.garfpay.modules.tenant.repository;

import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.enums.TipoOrganizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizacionRepository extends JpaRepository<Organizacion, UUID> {
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    List<Organizacion> findByTipoOrganizacion(TipoOrganizacion tipo);
}
