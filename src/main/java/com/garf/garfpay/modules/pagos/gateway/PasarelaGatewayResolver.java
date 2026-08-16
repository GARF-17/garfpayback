package com.garf.garfpay.modules.pagos.gateway;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PasarelaGatewayResolver {

    private final Map<NombreProveedor, IPasarelaPagoGateway> gatewaysPorProveedor;

    public PasarelaGatewayResolver(List<IPasarelaPagoGateway> gateways) {
        this.gatewaysPorProveedor = gateways.stream()
                .collect(Collectors.toMap(IPasarelaPagoGateway::proveedor, g -> g));
    }

    public IPasarelaPagoGateway resolver(NombreProveedor proveedor) {
        IPasarelaPagoGateway gateway = gatewaysPorProveedor.get(proveedor);
        if (gateway == null) {
            throw new BusinessRuleException("El proveedor de pago " + proveedor + " no tiene una integración configurada.");
        }
        return gateway;
    }
}