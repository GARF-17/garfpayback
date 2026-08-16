package com.garf.garfpay.modules.pagos.gateway.impl;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.gateway.dto.SolicitudCargoGatewayDTO;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference; // <-- IMPORTANTE
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class YapeGateway extends AbstractHttpPasarelaGateway {

    private final WebClient webClient;
    private final String apiKey;

    public YapeGateway(WebClient.Builder builder,
                       @Value("${psp.yape.base-url:https://api.yape.com.pe/v1}") String baseUrl,
                       @Value("${psp.yape.api-key:}") String apiKey) {
        this.webClient = construirCliente(builder, baseUrl);
        this.apiKey = apiKey;
    }

    @Override
    public NombreProveedor proveedor() {
        return NombreProveedor.YAPE;
    }

    @Override
    public ResultadoGatewayDTO cobrar(SolicitudCargoGatewayDTO solicitud) {
        return ejecutarConManejoDeErrores(() -> {
            // El pagador confirma la operación desde su app Yape usando el código
            // de confirmación (OTP) generado por el checkout; ese código llega
            // en los metadatos de la solicitud de cobro.
            String codigoConfirmacion = String.valueOf(
                    solicitud.metadatos() != null ? solicitud.metadatos().get("codigoConfirmacion") : null);

            Map<String, Object> payload = Map.of(
                    "amount", solicitud.monto(),
                    "currency", solicitud.moneda(),
                    "externalId", solicitud.claveIdempotencia(),
                    "confirmationCode", codigoConfirmacion
            );

            // CORRECCIÓN: ParameterizedTypeReference
            Map<String, Object> respuesta = webClient.post()
                    .uri("/payments")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            String idPago = String.valueOf(respuesta.get("paymentId"));
            return ResultadoGatewayDTO.exito(idPago, "YAPE-" + idPago, UUID.randomUUID().toString(), respuesta); // Ya no se necesita Map.copyOf
        });
    }

    @Override
    public ResultadoGatewayDTO reembolsar(String idTransaccionProveedor, BigDecimal monto, String motivo) {
        return ejecutarConManejoDeErrores(() -> {

            // CORRECCIÓN: ParameterizedTypeReference
            Map<String, Object> respuesta = webClient.post()
                    .uri("/payments/{id}/refund", idTransaccionProveedor)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("amount", monto))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            return ResultadoGatewayDTO.exito(idTransaccionProveedor, "REFUND-YAPE-" + idTransaccionProveedor,
                    null, respuesta); // Ya no se necesita Map.copyOf
        });
    }

    @Override
    public ResultadoGatewayDTO cobrarConTokenGuardado(String tokenProveedor, BigDecimal monto, String moneda, String claveIdempotencia) {
        // Yape no soporta cobro recurrente sin presencia del usuario en su app.
        // Se falla explícitamente para que el cron marque la suscripción como impaga
        // en vez de fingir un cobro que Yape nunca podría ejecutar de forma autónoma.
        throw new BusinessRuleException(
                "Yape no soporta cobro recurrente automático. La organización debe pagar manualmente cada ciclo o cambiar su método de pago predeterminado a una tarjeta.");
    }
}