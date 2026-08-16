package com.garf.garfpay.modules.facturacion.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record SuscribirOrganizacionRequestDTO(
        @NotNull UUID organizacionId,
        @NotNull Long planSuscripcionId,
        @NotNull LocalDate iniciaEl,
        @NotNull LocalDate terminaEl
) {}
