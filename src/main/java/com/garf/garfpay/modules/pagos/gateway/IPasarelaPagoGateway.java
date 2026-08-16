package com.garf.garfpay.modules.pagos.gateway;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.gateway.dto.ResultadoGatewayDTO;
import com.garf.garfpay.modules.pagos.gateway.dto.SolicitudCargoGatewayDTO;

import java.math.BigDecimal;

public interface IPasarelaPagoGateway {

    NombreProveedor proveedor();
    ResultadoGatewayDTO cobrar(SolicitudCargoGatewayDTO solicitud);
    ResultadoGatewayDTO reembolsar(String idTransaccionProveedor, BigDecimal monto, String motivo);
    ResultadoGatewayDTO cobrarConTokenGuardado(String tokenProveedor, BigDecimal monto, String moneda, String claveIdempotencia);
}