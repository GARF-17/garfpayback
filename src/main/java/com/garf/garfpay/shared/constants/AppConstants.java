package com.garf.garfpay.shared.constants;

/**
 * Clase que almacena todas las constantes globales del sistema GARFPAY.
 * Se declara como 'final' y con constructor privado para que no pueda ser instanciada.
 */
public final class AppConstants {

    private AppConstants() {
        // Previene la instanciación
    }

    // --- Headers y Seguridad ---
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // --- Trazabilidad ---
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String TRACE_ID_MDC_KEY = "traceId";

    // --- Roles Base (Coinciden con tu script de base de datos) ---
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ORG_ADMIN = "ORG_ADMIN";
    public static final String ROLE_TREASURER = "TREASURER";
    public static final String ROLE_COLLECTOR = "COLLECTOR";
    public static final String ROLE_USER = "USER";

    // --- Formatos Generales ---
    public static final String DEFAULT_TIMEZONE = "America/Lima";
}