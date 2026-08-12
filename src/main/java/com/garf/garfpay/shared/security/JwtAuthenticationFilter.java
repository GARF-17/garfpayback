package com.garf.garfpay.shared.security;

import com.garf.garfpay.shared.constants.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor // Genera un constructor solo con los atributos 'final'
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    // Spring Security usará esto para buscar el usuario en la Base de Datos
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraemos la cabecera de Autorización
        final String authHeader = request.getHeader(AppConstants.AUTH_HEADER);
        final String jwt;
        final String username;

        // 2. Si no hay token o no empieza con "Bearer ", lo dejamos pasar
        // (SecurityConfig decidirá luego si bloquea la petición o no)
        if (authHeader == null || !authHeader.startsWith(AppConstants.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Cortamos el "Bearer " (7 caracteres) para quedarnos con el token puro
        jwt = authHeader.substring(7);

        // 4. Extraemos el usuario (en tu caso será el correo o nombre_usuario)
        username = jwtService.extractUsername(jwt);

        // 5. Si el usuario existe en el token y aún no ha sido autenticado en este hilo
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Buscamos los datos del usuario (roles, estado, etc.) en la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Si el token es válido y pertenece a este usuario
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Creamos el ticket de acceso oficial de Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 7. Lo guardamos en el contexto. ¡El usuario ya está autenticado!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Continuamos con el resto de los filtros
        filterChain.doFilter(request, response);
    }
}