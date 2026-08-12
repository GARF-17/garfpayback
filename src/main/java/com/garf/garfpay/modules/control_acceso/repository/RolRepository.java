package com.garf.garfpay.modules.control_acceso.repository;

import com.garf.garfpay.modules.control_acceso.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional; // <-- Asegúrate de importar Optional

public interface RolRepository extends JpaRepository<Rol, Long> {

    boolean existsByCodigo(String codigo);
    Optional<Rol> findByCodigo(String codigo);

    @Query("SELECT DISTINCT r FROM Rol r LEFT JOIN FETCH r.permisos")
    List<Rol> findAllWithPermisos();
}