package com.garf.garfpay.modules.pagos.repository;

import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobro;
import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobroId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DestinoSolicitudCobroRepository extends JpaRepository<DestinoSolicitudCobro, DestinoSolicitudCobroId> {
    // Para listar todas las deudas de un usuario en específico
    List<DestinoSolicitudCobro> findByUsuario_UsuarioId(UUID usuarioId);
}