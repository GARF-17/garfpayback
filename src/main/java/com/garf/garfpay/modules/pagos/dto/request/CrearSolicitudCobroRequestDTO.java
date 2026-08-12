package com.garf.garfpay.modules.pagos.dto.request;

import com.garf.garfpay.modules.pagos.enums.TipoSolicitudPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CrearSolicitudCobroRequestDTO(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        @NotNull(message = "El tipo de solicitud es obligatorio")
        TipoSolicitudPago tipo,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        String moneda,
        Boolean permitePagoParcial,
        OffsetDateTime expiraEl,

        @NotEmpty(message = "Debe asignar al menos un usuario destino")
        List<UUID> usuariosDestinoIds
) {}