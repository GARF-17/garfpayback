package com.garf.garfpay.modules.identidad.repository;

import com.garf.garfpay.modules.identidad.entity.CodigoVerificacion;
import com.garf.garfpay.modules.identidad.enums.TipoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, UUID> {

    // Busca el último código generado activo
    Optional<CodigoVerificacion> findTopByUsuarioUsuarioIdAndTipoAndUsadoElIsNullAndExpiraElAfterOrderByCreadoElDesc(
            UUID usuarioId,
            TipoVerificacion tipo,
            LocalDateTime ahora
    );
}