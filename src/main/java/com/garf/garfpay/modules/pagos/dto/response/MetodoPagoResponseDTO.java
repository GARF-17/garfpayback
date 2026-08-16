package com.garf.garfpay.modules.pagos.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MetodoPagoResponseDTO(
        UUID metodoPagoId,
        UUID organizacionId,
        String proveedor,
        String marcaTarjeta,
        String ultimosCuatroDigitos,
        Boolean esPredeterminado,
        OffsetDateTime creadoEl
) {}