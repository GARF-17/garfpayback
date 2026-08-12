package com.garf.garfpay.modules.identidad.repository;

import com.garf.garfpay.modules.identidad.entity.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, UUID> {
    Optional<PerfilUsuario> findByCorreo(String correo);
    Optional<PerfilUsuario> findByNumeroDocumento(String numeroDocumento);
    boolean existsByCorreo(String correo);
    boolean existsByNumeroDocumento(String numeroDocumento);
}