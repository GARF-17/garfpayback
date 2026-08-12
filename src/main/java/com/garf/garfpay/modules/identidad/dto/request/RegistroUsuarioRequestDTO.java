package com.garf.garfpay.modules.identidad.dto.request;

import com.garf.garfpay.modules.identidad.enums.TipoDocumento;
import com.garf.garfpay.shared.validation.ValidEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequestDTO(
        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @ValidEnum(enumClass = TipoDocumento.class, message = "El tipo de documento debe ser DNI, CARNET_EXTRANJERIA, PASAPORTE o RUC")
        String tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        String numeroDocumento,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Debe ser un correo electrónico válido")
        String correo,

        String telefono, // Opcional por ahora según la BD

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
        String nombreUsuario,

        @NotBlank(message = "La contraseña es obligatoria")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "La clave debe tener al menos 8 caracteres, una letra y un número")
        String clave
) {}