package com.garf.garfpay.modules.pagos.repository;

import com.garf.garfpay.modules.pagos.entity.Reembolso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReembolsoRepository extends JpaRepository<Reembolso, UUID> {
}