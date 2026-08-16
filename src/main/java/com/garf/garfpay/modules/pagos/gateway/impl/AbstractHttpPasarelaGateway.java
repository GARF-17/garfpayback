package com.garf.garfpay.modules.pagos.gateway.impl;

import com.garf.garfpay.modules.pagos.gateway.IPasarelaPagoGateway;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractHttpPasarelaGateway implements IPasarelaPagoGateway {

    protected static final Duration TIMEOUT_PSP = Duration.ofSeconds(15);

    protected WebClient construirCliente(WebClient.Builder builder, String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }

    protected ResultadoGatewayDTO ejecutarConManejoDeErrores(Supplier<ResultadoGatewayDTO> llamada) {
        try {
            return llamada.get();
        } catch (WebClientResponseException httpEx) {
            log.error("El proveedor {} respondió con error HTTP {}: {}",
                    proveedor(), httpEx.getStatusCode(), httpEx.getResponseBodyAsString());
            return ResultadoGatewayDTO.fallo(
                    "Error del proveedor (" + httpEx.getStatusCode() + "): " + httpEx.getResponseBodyAsString(),
                    Map.of("httpStatus", httpEx.getStatusCode().value(), "body", httpEx.getResponseBodyAsString()));
        } catch (Exception ex) {
            log.error("Fallo de comunicación con el proveedor {}: {}", proveedor(), ex.getMessage(), ex);
            return ResultadoGatewayDTO.fallo("Error de comunicación con el proveedor: " + ex.getMessage(), Map.of());
        }
    }
}