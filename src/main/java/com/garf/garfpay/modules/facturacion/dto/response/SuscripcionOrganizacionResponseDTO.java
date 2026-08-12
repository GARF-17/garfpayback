package com.garf.garfpay.modules.facturacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SuscripcionOrganizacionResponseDTO(
        UUID suscripcionOrganizacionId,
        UUID organizacionId,
        String nombrePlan,
        BigDecimal precioPlan,
        LocalDate iniciaEl,
        LocalDate terminaEl,
        Boolean estaActiva
) {}