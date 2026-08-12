package com.garf.garfpay.modules.tenant.dto.response;

import java.util.UUID;

public record OrganizacionResponseDTO(
        UUID organizacionId,
        String razonSocial,
        String tipoOrganizacion,
        String categoria,
        String documentoIdentidad,
        String correo,
        String telefono,
        String urlLogo,
        String estado
) {}