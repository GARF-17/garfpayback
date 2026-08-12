package com.garf.garfpay.modules.identidad.repository;

import com.garf.garfpay.modules.identidad.entity.SesionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, UUID> {
    Optional<SesionUsuario> findByHashTokenRefresco(String hashTokenRefresco);
    List<SesionUsuario> findByUsuario_UsuarioIdAndEstaActivaTrue(UUID usuarioId);
}