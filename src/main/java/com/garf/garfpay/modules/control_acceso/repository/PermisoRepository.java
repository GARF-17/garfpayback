package com.garf.garfpay.modules.control_acceso.repository;

import com.garf.garfpay.modules.control_acceso.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    // Permite buscar varios permisos de golpe pasando una lista de IDs
    Set<Permiso> findByPermisoIdIn(Set<Long> permisosIds);
}