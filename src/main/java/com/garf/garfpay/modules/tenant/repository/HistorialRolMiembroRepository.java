package com.garf.garfpay.modules.tenant.repository;

import com.garf.garfpay.modules.tenant.entity.HistorialRolMiembro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialRolMiembroRepository extends JpaRepository<HistorialRolMiembro, UUID> {
    List<HistorialRolMiembro> findByMiembro_Id_OrganizacionIdAndMiembro_Id_UsuarioIdOrderByCreadoElDesc(
            UUID organizacionId, UUID usuarioId);
}