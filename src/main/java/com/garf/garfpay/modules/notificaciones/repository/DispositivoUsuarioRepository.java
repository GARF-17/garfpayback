package com.garf.garfpay.modules.notificaciones.repository;

import com.garf.garfpay.modules.notificaciones.entity.DispositivoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface DispositivoUsuarioRepository extends JpaRepository<DispositivoUsuario, UUID> {
    Optional<DispositivoUsuario> findByUsuario_UsuarioIdAndTokenPush(UUID usuarioId, String tokenPush);
}