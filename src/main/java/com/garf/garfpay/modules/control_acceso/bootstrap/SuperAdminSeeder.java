package com.garf.garfpay.modules.control_acceso.bootstrap;

import com.garf.garfpay.modules.control_acceso.entity.Rol;
import com.garf.garfpay.modules.control_acceso.entity.UsuarioRol;
import com.garf.garfpay.modules.control_acceso.repository.RolRepository;
import com.garf.garfpay.modules.identidad.entity.PerfilUsuario;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.enums.EstadoUsuario;
import com.garf.garfpay.modules.identidad.enums.TipoDocumento;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminSeeder implements CommandLineRunner {

    private final UsuarioAppRepository usuarioAppRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // Leemos las variables seguras desde el application.yml
    @Value("${app.admin.nombres}") private String adminNombres;
    @Value("${app.admin.apellidos}") private String adminApellidos;
    @Value("${app.admin.documento}") private String adminDocumento;
    @Value("${app.admin.correo}") private String adminCorreo;
    @Value("${app.admin.username}") private String adminUsername;
    @Value("${app.admin.password}") private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. Verificamos si el Super Admin ya existe. Si ya existe, no hacemos nada.
        if (usuarioAppRepository.findByNombreUsuario(adminUsername).isPresent()) {
            log.info(" El Super Admin ya está configurado en la base de datos.");
            return;
        }

        log.info(" Inicializando el Súper Administrador del sistema...");

        // 2. Buscamos el rol SUPER_ADMIN que Flyway ya creó
        Rol rolSuperAdmin = rolRepository.findByCodigo("SUPER_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ERROR CRÍTICO: No se encontró el rol SUPER_ADMIN en BD."));

        // 3. Creamos el Perfil
        PerfilUsuario perfil = PerfilUsuario.builder()
                .nombres(adminNombres)
                .apellidos(adminApellidos)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento(adminDocumento)
                .correo(adminCorreo)
                .correoVerificado(true)
                .telefono("000000000")
                .telefonoVerificado(true)
                .build();

        // 4. Creamos el Usuario y encriptamos la clave en memoria
        UsuarioApp usuarioAdmin = UsuarioApp.builder()
                .perfil(perfil)
                .nombreUsuario(adminUsername)
                .claveHash(passwordEncoder.encode(adminPassword))
                .estado(EstadoUsuario.ACTIVO)
                .intentosFallidosLogin(0)
                .mfaHabilitado(false)
                .build();

        // 5. Asignamos el rol (Usando setters en lugar de Builder para evitar el error de Lombok)
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setId(new com.garf.garfpay.modules.control_acceso.entity.UsuarioRolId()); // Llave vacía que JPA llenará
        usuarioRol.setUsuario(usuarioAdmin);
        usuarioRol.setRol(rolSuperAdmin);

        usuarioAdmin.getRoles().add(usuarioRol);

        // 6. Guardamos en Base de Datos
        usuarioAppRepository.save(usuarioAdmin);

        log.info("Súper Administrador creado con éxito. ¡Sistema protegido!");
    }
}