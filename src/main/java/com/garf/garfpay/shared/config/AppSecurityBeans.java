package com.garf.garfpay.shared.config;

import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class AppSecurityBeans {

    private final UsuarioAppRepository usuarioRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // 👇 USAMOS EL NUEVO MÉTODO CON ROLES CARGADOS
            var usuario = usuarioRepository.findByNombreUsuarioWithRoles(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            String[] rolesArray = usuario.getRoles().stream()
                    .map(ur -> ur.getRol().getCodigo())
                    .toArray(String[]::new);

            if (rolesArray.length == 0) {
                throw new UsernameNotFoundException("El usuario no tiene roles asignados.");
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(usuario.getNombreUsuario())
                    .password(usuario.getClaveHash())
                    .roles(rolesArray)
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}