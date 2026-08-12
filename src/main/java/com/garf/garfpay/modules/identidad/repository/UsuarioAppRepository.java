package com.garf.garfpay.modules.identidad.repository;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioAppRepository extends JpaRepository<UsuarioApp, UUID> {
    Optional<UsuarioApp> findByNombreUsuario(String nombreUsuario);

    @Query("SELECT u FROM UsuarioApp u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.rol WHERE u.nombreUsuario = :nombreUsuario")
    Optional<UsuarioApp> findByNombreUsuarioWithRoles(@Param("nombreUsuario") String nombreUsuario);

    @Query("SELECT u FROM UsuarioApp u LEFT JOIN FETCH u.perfil WHERE u.nombreUsuario = :nombreUsuario")
    Optional<UsuarioApp> findByNombreUsuarioWithPerfil(@Param("nombreUsuario") String nombreUsuario);
}