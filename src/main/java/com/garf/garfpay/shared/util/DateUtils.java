package com.garf.garfpay.shared.util;

import com.garf.garfpay.shared.constants.AppConstants;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private DateUtils() {
        // Prevenir instanciación
    }

    /**
     * Obtiene la fecha y hora actual en la zona horaria del sistema (Perú).
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(AppConstants.DEFAULT_TIMEZONE));
    }

    /**
     * Formatea un LocalDateTime a un String estándar ISO 8601 (Ej: "2026-07-28T14:30:00-05:00")
     * Muy útil para enviarlo al frontend móvil.
     */
    public static String formatIso(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of(AppConstants.DEFAULT_TIMEZONE));
        return zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}