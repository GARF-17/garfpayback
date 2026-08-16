package com.garf.garfpay.modules.pagos.gateway.dto;

import java.util.Map;

public record ResultadoGatewayDTO(
        boolean exitoso,
        String idTransaccionProveedor,
        String referenciaProveedor,
        String idTraza,
        String motivoFallo,
        Map<String, Object> respuestaCruda
) {
    public static ResultadoGatewayDTO exito(String idProveedor, String referencia, String traza, Map<String, Object> cruda) {
        return new ResultadoGatewayDTO(true, idProveedor, referencia, traza, null, cruda);
    }

    public static ResultadoGatewayDTO fallo(String motivo, Map<String, Object> cruda) {
        return new ResultadoGatewayDTO(false, null, null, null, motivo, cruda);
    }
}