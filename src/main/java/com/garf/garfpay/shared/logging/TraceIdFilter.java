package com.garf.garfpay.shared.logging;

import com.garf.garfpay.shared.constants.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Verificamos si React Native (o la pasarela) ya nos mandó un Trace ID
        String traceId = request.getHeader(AppConstants.TRACE_ID_HEADER);

        // Si no mandó ninguno, generamos uno nosotros
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        // MDC (Mapped Diagnostic Context): Instruye a Spring a incluir este ID en cada línea de log
        MDC.put(AppConstants.TRACE_ID_MDC_KEY, traceId);

        // Se lo agregamos a los Headers de la respuesta para que la app móvil lo pueda mostrar si hay un error
        response.setHeader(AppConstants.TRACE_ID_HEADER, traceId);

        try {
            // Continuamos con el flujo normal de la petición
            filterChain.doFilter(request, response);
        } finally {
            // Limpiar el contexto al terminar para no mezclar IDs de diferentes usuarios
            MDC.remove(AppConstants.TRACE_ID_MDC_KEY);
        }
    }
}