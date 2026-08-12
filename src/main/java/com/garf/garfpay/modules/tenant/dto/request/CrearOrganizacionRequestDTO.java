package com.garf.garfpay.modules.tenant.dto.request;

import com.garf.garfpay.modules.tenant.enums.CategoriaOrganizacion;
import com.garf.garfpay.modules.tenant.enums.TipoOrganizacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearOrganizacionRequestDTO(
        @NotBlank(message = "La razón social o nombre público es obligatorio")
        @Size(max = 150)
        String razonSocial,

        @NotNull(message = "El tipo de organización es obligatorio (PERSONAL, COMUNIDAD, CORPORATIVO)")
        TipoOrganizacion tipoOrganizacion,

        @NotNull(message = "La categoría es obligatoria")
        CategoriaOrganizacion categoria,

        @Size(max = 20)
        String documentoIdentidad,

        @Email(message = "Formato de correo inválido")
        @Size(max = 120)
        String correo,

        @Size(max = 20)
        String telefono,

        String direccion,
        String urlLogo
) {}