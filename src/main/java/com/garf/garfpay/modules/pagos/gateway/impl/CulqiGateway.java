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
import java.util.Map;
import java.util.UUID;

/**
 * Adaptador hacia Culqi. La URL base y las credenciales se externalizan por configuración
 * (application-{profile}.yml), de modo que en `dev` se apunta al sandbox público de Culqi
 * y en `prod` a su API real, sin cambiar una sola línea de código del dominio.
 */
@Component
public class CulqiGateway extends AbstractHttpPasarelaGateway {

    private final WebClient webClient;
    private final String secretKey;

    public CulqiGateway(WebClient.Builder builder,
                        @Value("${psp.culqi.base-url:https://api.culqi.com/v2}") String baseUrl,
                        @Value("${psp.culqi.secret-key:}") String secretKey) {
        this.webClient = construirCliente(builder, baseUrl);
        this.secretKey = secretKey;
    }

    @Override
    public NombreProveedor proveedor() {
        return NombreProveedor.CULQI;
    }

    @Override
    public ResultadoGatewayDTO cobrar(SolicitudCargoGatewayDTO solicitud) {
        return ejecutarConManejoDeErrores(() -> {
            Map<String, Object> payload = Map.of(
                    "amount", solicitud.monto().multiply(BigDecimal.valueOf(100)).intValue(), // Culqi trabaja en céntimos
                    "currency_code", solicitud.moneda(),
                    "external_id", solicitud.claveIdempotencia(),
                    "metadata", solicitud.metadatos() == null ? Map.of() : solicitud.metadatos()
            );

            Map<String, Object> respuesta = webClient.post()
                    .uri("/charges")
                    .header("Authorization", "Bearer " + secretKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            String idCargo = String.valueOf(respuesta.get("id"));
            return ResultadoGatewayDTO.exito(
                    idCargo,
                    "CULQI-" + idCargo,
                    UUID.randomUUID().toString(),
                    respuesta); // Ya no hace falta Map.copyOf, ya está tipado
        });
    }

    @Override
    public ResultadoGatewayDTO reembolsar(String idTransaccionProveedor, BigDecimal monto, String motivo) {
        return ejecutarConManejoDeErrores(() -> {
            Map<String, Object> payload = Map.of("amount", monto.multiply(BigDecimal.valueOf(100)).intValue());

            Map<String, Object> respuesta = webClient.post()
                    .uri("/charges/{id}/refunds", idTransaccionProveedor)
                    .header("Authorization", "Bearer " + secretKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            String idReembolso = String.valueOf(respuesta.get("id"));
            return ResultadoGatewayDTO.exito(idReembolso, "REFUND-" + idReembolso, null, respuesta);
        });
    }

    @Override
    public ResultadoGatewayDTO cobrarConTokenGuardado(String tokenProveedor, BigDecimal monto, String moneda, String claveIdempotencia) {
        return ejecutarConManejoDeErrores(() -> {
            Map<String, Object> payload = Map.of(
                    "amount", monto.multiply(BigDecimal.valueOf(100)).intValue(),
                    "currency_code", moneda,
                    "source_id", tokenProveedor,
                    "external_id", claveIdempotencia
            );

            Map<String, Object> respuesta = webClient.post()
                    .uri("/charges")
                    .header("Authorization", "Bearer " + secretKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            String idCargo = String.valueOf(respuesta.get("id"));
            return ResultadoGatewayDTO.exito(idCargo, "CULQI-" + idCargo, UUID.randomUUID().toString(), respuesta);
        });
    }
}