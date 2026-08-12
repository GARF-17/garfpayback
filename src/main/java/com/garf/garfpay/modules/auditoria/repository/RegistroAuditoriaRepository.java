package com.garf.garfpay.modules.auditoria.repository;

import com.garf.garfpay.modules.auditoria.entity.RegistroAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, UUID> {

    // Buscar el historial de cambios de una entidad específica (Ej: una Organización o un Pago)
    Page<RegistroAuditoria> findByIdEntidadOrderByCreadoElDesc(UUID idEntidad, Pageable pageable);
    // Buscar qué ha hecho un usuario específico en el sistema
    Page<RegistroAuditoria> findByUsuario_UsuarioIdOrderByCreadoElDesc(UUID usuarioId, Pageable pageable);
}