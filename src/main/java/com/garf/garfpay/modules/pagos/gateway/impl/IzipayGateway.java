package com.garf.garfpay.modules.pagos.gateway.impl;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.gateway.dto.SolicitudCargoGatewayDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Component
public class IzipayGateway extends AbstractHttpPasarelaGateway {

    private final WebClient webClient;
    private final String shopId;
    private final String apiKey;

    public IzipayGateway(WebClient.Builder builder,
                         @Value("${psp.izipay.base-url:https://api.micuentaweb.pe}") String baseUrl,
                         @Value("${psp.izipay.shop-id:}") String shopId,
                         @Value("${psp.izipay.api-key:}") String apiKey) {
        this.webClient = construirCliente(builder, baseUrl);
        this.shopId = shopId;
        this.apiKey = apiKey;
    }

    @Override
    public NombreProveedor proveedor() {
        return NombreProveedor.IZIPAY;
    }

    private String basicAuthHeader() {
        String credenciales = shopId + ":" + apiKey;
        return "Basic " + Base64.getEncoder().encodeToString(credenciales.getBytes());
    }

    @Override
    public ResultadoGatewayDTO cobrar(SolicitudCargoGatewayDTO solicitud) {
        return ejecutarConManejoDeErrores(() -> {
            Map<String, Object> payload = Map.of(
                    "amount", solicitud.monto().multiply(BigDecimal.valueOf(100)).intValue(),
                    "currency", solicitud.moneda(),
                    "orderId", solicitud.claveIdempotencia(),
                    "formToken", solicitud.metadatos() != null ? solicitud.metadatos().get("formToken") : null
            );

            // CORRECCIÓN: ParameterizedTypeReference
            Map<String, Object> respuesta = webClient.post()
                    .uri("/api-payment/V4/Charge/CreatePayment")
                    .header("Authorization", basicAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            // CORRECCIÓN: Casteo fuerte a Map<String, Object>
            @SuppressWarnings("unchecked")
            Map<String, Object> answer = (Map<String, Object>) respuesta.get("answer");

            @SuppressWarnings("unchecked")
            Map<String, Object> transaccion = (Map<String, Object>) ((java.util.List<?>) answer.get("transactions")).get(0);

            String idTransaccion = String.valueOf(transaccion.get("uuid"));

            return ResultadoGatewayDTO.exito(idTransaccion, "IZIPAY-" + idTransaccion,
                    UUID.randomUUID().toString(), respuesta); // Ya no se necesita Map.copyOf
        });
    }

    @Override
    public ResultadoGatewayDTO reembolsar(String idTransaccionProveedor, BigDecimal monto, String motivo) {
        return ejecutarConManejoDeErrores(() -> {
            Map<String, Object> payload = Map.of(
                    "uuid", idTransaccionProveedor,
                    "amount", monto.multiply(BigDecimal.valueOf(100)).intValue()
            );

            // CORRECCIÓN: ParameterizedTypeReference
            Map<String, Object> respuesta = webClient.post()
                    .uri("/api-payment/V4/Transaction/Cancel")
                    .header("Authorization", basicAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            return ResultadoGatewayDTO.exito(idTransaccionProveedor, "REFUND-IZIPAY-" + idTransaccionProveedor,
                    null, respuesta); // Ya no se necesita Map.copyOf
        });
    }

    @Override
    public ResultadoGatewayDTO cobrarConTokenGuardado(String tokenProveedor, BigDecimal monto, String moneda, String claveIdempotencia) {
        return ejecutarConManejoDeErrores(() -> {
            Map<String, Object> payload = Map.of(
                    "amount", monto.multiply(BigDecimal.valueOf(100)).intValue(),
                    "currency", moneda,
                    "orderId", claveIdempotencia,
                    "paymentMethodToken", tokenProveedor
            );

            // CORRECCIÓN: ParameterizedTypeReference
            Map<String, Object> respuesta = webClient.post()
                    .uri("/api-payment/V4/Charge/CreatePayment")
                    .header("Authorization", basicAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            // CORRECCIÓN: Casteo fuerte a Map<String, Object>
            @SuppressWarnings("unchecked")
            Map<String, Object> answer = (Map<String, Object>) respuesta.get("answer");

            @SuppressWarnings("unchecked")
            Map<String, Object> transaccion = (Map<String, Object>) ((java.util.List<?>) answer.get("transactions")).get(0);

            String idTransaccion = String.valueOf(transaccion.get("uuid"));

            return ResultadoGatewayDTO.exito(idTransaccion, "IZIPAY-REC-" + idTransaccion, null, respuesta); // Ya no se necesita Map.copyOf
        });
    }
}