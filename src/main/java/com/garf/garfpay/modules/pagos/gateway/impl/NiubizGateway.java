package com.garf.garfpay.modules.pagos.gateway.impl;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.gateway.dto.SolicitudCargoGatewayDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class NiubizGateway extends AbstractHttpPasarelaGateway {

    private final WebClient webClient;
    private final String merchantId;
    private final String accessKey;

    public NiubizGateway(WebClient.Builder builder,
                         @Value("${psp.niubiz.base-url:https://apitestenv.vnforapps.com}") String baseUrl,
                         @Value("${psp.niubiz.merchant-id:}") String merchantId,
                         @Value("${psp.niubiz.access-key:}") String accessKey) {
        this.webClient = construirCliente(builder, baseUrl);
        this.merchantId = merchantId;
        this.accessKey = accessKey;
    }

    @Override
    public NombreProveedor proveedor() {
        return NombreProveedor.NIUBIZ;
    }

    private String obtenerTokenSeguridad() {
        return webClient.get()
                .uri("/api.security/v1/security")
                .header("Authorization", accessKey)
                .retrieve()
                .bodyToMono(String.class)
                .block(TIMEOUT_PSP);
    }

    private String generarPurchaseNumber() {
        String time = String.valueOf(System.currentTimeMillis());
        return time.substring(time.length() - 12);
    }

    @Override
    public ResultadoGatewayDTO cobrar(SolicitudCargoGatewayDTO solicitud) {

        // 🚀 MOCK DE DESARROLLO PARA POSTMAN (COBRO DIRECTO)
        // Como estamos probando en Postman sin el frontend de Angular/Figma,
        // encendemos este interruptor para probar toda la lógica de base de datos.
        boolean modoDesarrollo = true; // <-- Pon esto en 'false' cuando conectes Angular
        if (modoDesarrollo) {
            log.info("SIMULANDO cobro directo exitoso (Dev Bypass activado)");
            String idSimulado = "sim_dir_" + System.currentTimeMillis();
            return ResultadoGatewayDTO.exito(idSimulado, "NIUBIZ-" + idSimulado,
                    UUID.randomUUID().toString(), Map.of("estado", "Autorizado", "mensaje", "Bypass de desarrollo activado"));
        }

        return ejecutarConManejoDeErrores(() -> {
            String tokenSesion = String.valueOf(
                    solicitud.metadatos() != null ? solicitud.metadatos().get("tokenSesion") : null);

            String tokenSeguridad = obtenerTokenSeguridad();

            Map<String, Object> payload = Map.of(
                    "channel", "web",
                    "captureType", "manual",
                    "countable", true,
                    "order", Map.of(
                            "purchaseNumber", generarPurchaseNumber(),
                            "amount", solicitud.monto(),
                            "currency", solicitud.moneda()
                    )
            );

            Map<String, Object> respuesta = webClient.post()
                    .uri("/api.authorization/v3/authorization/ecommerce/" + merchantId)
                    .header("Authorization", tokenSeguridad)
                    .header("X-Session-Token", tokenSesion)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            @SuppressWarnings("unchecked")
            Map<String, Object> orden = (Map<String, Object>) respuesta.get("order");
            String idTransaccion = String.valueOf(orden.get("transactionId"));

            return ResultadoGatewayDTO.exito(idTransaccion, "NIUBIZ-" + idTransaccion,
                    UUID.randomUUID().toString(), respuesta);
        });
    }

    @Override
    public ResultadoGatewayDTO reembolsar(String idTransaccionProveedor, BigDecimal monto, String motivo) {
        return ejecutarConManejoDeErrores(() -> {
            String tokenSeguridad = obtenerTokenSeguridad();

            Map<String, Object> payload = Map.of(
                    "channel", "web",
                    "amount", monto
            );

            Map<String, Object> respuesta = webClient.post()
                    .uri("/api.authorization/v3/refund/" + merchantId + "/" + idTransaccionProveedor)
                    .header("Authorization", tokenSeguridad)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            return ResultadoGatewayDTO.exito(idTransaccionProveedor, "REFUND-NIUBIZ-" + idTransaccionProveedor,
                    null, respuesta);
        });
    }

    @Override
    public ResultadoGatewayDTO cobrarConTokenGuardado(String tokenProveedor, BigDecimal monto, String moneda, String claveIdempotencia) {

        // 🚀 MOCK DE DESARROLLO PARA POSTMAN (COBRO RECURRENTE)
        if (tokenProveedor != null && tokenProveedor.startsWith("tkn_test_")) {
            log.info("SIMULANDO cobro recurrente con token de prueba: {}", tokenProveedor);
            String idSimulado = "sim_rec_" + System.currentTimeMillis();
            return ResultadoGatewayDTO.exito(idSimulado, "NIUBIZ-REC-" + idSimulado, null,
                    Map.of("estado", "Autorizado", "mensaje", "Bypass de desarrollo activado"));
        }

        return ejecutarConManejoDeErrores(() -> {
            String tokenSeguridad = obtenerTokenSeguridad();

            Map<String, Object> payload = Map.of(
                    "channel", "recurrent",
                    "captureType", "manual",
                    "recurrence", Map.of("action", "initial", "maxAmount", monto),
                    "order", Map.of(
                            "purchaseNumber", generarPurchaseNumber(),
                            "amount", monto,
                            "currency", moneda
                    ),
                    "instrumentId", tokenProveedor
            );

            Map<String, Object> respuesta = webClient.post()
                    .uri("/api.authorization/v3/authorization/ecommerce/" + merchantId)
                    .header("Authorization", tokenSeguridad)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            @SuppressWarnings("unchecked")
            Map<String, Object> orden = (Map<String, Object>) respuesta.get("order");
            String idTransaccion = String.valueOf(orden.get("transactionId"));

            return ResultadoGatewayDTO.exito(idTransaccion, "NIUBIZ-REC-" + idTransaccion, null, respuesta);
        });
    }
}