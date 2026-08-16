package com.garf.garfpay.modules.pagos.gateway.impl;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.gateway.IPasarelaPagoGateway;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.gateway.dto.SolicitudCargoGatewayDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Profile("test")
@Component
public class FakePasarelaGateway implements IPasarelaPagoGateway {

    @Override
    public NombreProveedor proveedor() {
        return NombreProveedor.CULQI;
    }

    @Override
    public ResultadoGatewayDTO cobrar(SolicitudCargoGatewayDTO solicitud) {
        String id = "FAKE-" + UUID.randomUUID();
        return ResultadoGatewayDTO.exito(id, "FAKE-REF-" + id, UUID.randomUUID().toString(), Map.of("simulated", true));
    }

    @Override
    public ResultadoGatewayDTO reembolsar(String idTransaccionProveedor, BigDecimal monto, String motivo) {
        return ResultadoGatewayDTO.exito("FAKE-REFUND-" + idTransaccionProveedor, "REFUND-OK", null, Map.of());
    }

    @Override
    public ResultadoGatewayDTO cobrarConTokenGuardado(String tokenProveedor, BigDecimal monto, String moneda, String claveIdempotencia) {
        String id = "FAKE-REC-" + UUID.randomUUID();
        return ResultadoGatewayDTO.exito(id, "FAKE-REC-REF-" + id, null, Map.of("simulated", true));
    }
}