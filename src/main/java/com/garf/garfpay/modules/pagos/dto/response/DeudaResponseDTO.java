package com.garf.garfpay.modules.pagos.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DeudaResponseDTO(
        UUID solicitudCobroId,
        String tituloCobro,
        String descripcionCobro,
        BigDecimal montoOriginal,
        BigDecimal montoPersonalizado,
        String moneda,
        String estado,
        OffsetDateTime expiraEl,
        OffsetDateTime pagadoEl
) {}