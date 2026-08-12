package com.garf.garfpay.modules.pagos.repository;

import com.garf.garfpay.modules.pagos.entity.EventoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EventoProveedorRepository extends JpaRepository<EventoProveedor, UUID> {
}