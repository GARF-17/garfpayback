package com.garf.garfpay.modules.contabilidad.repository;

import com.garf.garfpay.modules.contabilidad.entity.Tarifario;
import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarifarioRepository extends JpaRepository<Tarifario, UUID> {

    @Query("SELECT t FROM Tarifario t " +
            "WHERE (t.organizacion.organizacionId = :organizacionId OR t.organizacion IS NULL) " +
            "AND t.proveedor = :proveedor " +
            "AND t.vigenteDesde <= :fecha " +
            "AND (t.vigenteHasta IS NULL OR t.vigenteHasta > :fecha) " +
            "ORDER BY t.organizacion.organizacionId NULLS LAST")
    List<Tarifario> buscarTarifariosVigentes(
            @Param("organizacionId") UUID organizacionId,
            @Param("proveedor") NombreProveedor proveedor,
            @Param("fecha") OffsetDateTime fecha);

    List<Tarifario> findByOrganizacion_OrganizacionId(UUID organizacionId);
}