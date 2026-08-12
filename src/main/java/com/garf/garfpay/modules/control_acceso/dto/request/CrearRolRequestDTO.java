package com.garf.garfpay.modules.control_acceso.dto.request;

import com.garf.garfpay.modules.control_acceso.enums.AmbitoRol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

public record CrearRolRequestDTO(
        @NotBlank(message = "El código es obligatorio")
        @Pattern(regexp = "^[A-Z_]+$", message = "El código solo puede contener letras mayúsculas y guiones bajos (ej. GESTOR_PAGOS)")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "El ámbito del rol es obligatorio (PLATAFORMA u ORGANIZACION)")
        AmbitoRol ambito,

        Set<Long> permisoIds
) {}