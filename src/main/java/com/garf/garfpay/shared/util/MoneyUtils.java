package com.garf.garfpay.shared.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {

    private MoneyUtils() {
        // Prevenir instanciación
    }

    /**
     * Estandariza cualquier monto a 2 decimales usando redondeo HALF_UP (El estándar bancario).
     */
    public static BigDecimal formatAmount(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Verifica si un monto es mayor a cero.
     */
    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}