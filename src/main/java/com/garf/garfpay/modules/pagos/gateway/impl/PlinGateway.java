package com.garf.garfpay.modules.pagos.gateway.impl;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.gateway.dto.SolicitudCargoGatewayDTO;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class PlinGateway extends AbstractHttpPasarelaGateway {

    private final WebClient webClient;
    private final String apiKey;

    public PlinGateway(WebClient.Builder builder,
                       @Value("${psp.plin.base-url:https://api.plin.pe/v1}") String baseUrl,
                       @Value("${psp.plin.api-key:}") String apiKey) {
        this.webClient = construirCliente(builder, baseUrl);
        this.apiKey = apiKey;
    }

    @Override
    public NombreProveedor proveedor() {
        return NombreProveedor.PLIN;
    }

    @Override
    public ResultadoGatewayDTO cobrar(SolicitudCargoGatewayDTO solicitud) {
        return ejecutarConManejoDeErrores(() -> {
            // Al igual que Yape, Plin suele requerir un código de confirmación o número de teléfono
            // que se captura en el frontend y viaja en los metadatos.
            String codigoConfirmacion = String.valueOf(
                    solicitud.metadatos() != null ? solicitud.metadatos().get("codigoConfirmacion") : null);

            // Añadimos también el teléfono por si la API de Plin lo exige
            String numeroTelefono = String.valueOf(
                    solicitud.metadatos() != null ? solicitud.metadatos().get("numeroTelefono") : null);

            Map<String, Object> payload = Map.of(
                    "amount", solicitud.monto(),
                    "currency", solicitud.moneda(),
                    "externalTransactionId", solicitud.claveIdempotencia(),
                    "confirmationCode", codigoConfirmacion,
                    "phoneNumber", numeroTelefono
            );

            // Uso de ParameterizedTypeReference para evitar el error de tipos Incompatibles
            Map<String, Object> respuesta = webClient.post()
                    .uri("/transactions/pay")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            String idPago = String.valueOf(respuesta.get("transactionId"));
            return ResultadoGatewayDTO.exito(idPago, "PLIN-" + idPago, UUID.randomUUID().toString(), respuesta);
        });
    }

    @Override
    public ResultadoGatewayDTO reembolsar(String idTransaccionProveedor, BigDecimal monto, String motivo) {
        return ejecutarConManejoDeErrores(() -> {

            // Uso de ParameterizedTypeReference
            Map<String, Object> respuesta = webClient.post()
                    .uri("/transactions/{id}/refund", idTransaccionProveedor)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("amount", monto, "reason", motivo))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(TIMEOUT_PSP);

            return ResultadoGatewayDTO.exito(idTransaccionProveedor, "REFUND-PLIN-" + idTransaccionProveedor,
                    null, respuesta);
        });
    }

    @Override
    public ResultadoGatewayDTO cobrarConTokenGuardado(String tokenProveedor, BigDecimal monto, String moneda, String claveIdempotencia) {
        // Plin, al igual que Yape, es una billetera P2P/P2M que requiere la autorización activa
        // del usuario (abrir la app, confirmar pago). No soporta cobros recurrentes en background (tokenizados).
        throw new BusinessRuleException(
                "Plin no soporta cobro recurrente automático. La organización debe pagar manualmente cada ciclo o cambiar su método de pago predeterminado a una tarjeta.");
    }
}