package com.garf.garfpay.modules.pagos.mapper;

import com.garf.garfpay.modules.pagos.dto.request.CrearSolicitudCobroRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.DeudaResponseDTO;
import com.garf.garfpay.modules.pagos.dto.response.ReembolsoResponseDTO;
import com.garf.garfpay.modules.pagos.dto.response.SolicitudCobroResponseDTO;
import com.garf.garfpay.modules.pagos.dto.response.TransaccionResponseDTO;
import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobro;
import com.garf.garfpay.modules.pagos.entity.Reembolso;
import com.garf.garfpay.modules.pagos.entity.SolicitudCobro;
import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PagosMapper {

    // Mapeos de Solicitud de Cobro
    SolicitudCobro toSolicitudCobroEntity(CrearSolicitudCobroRequestDTO dto);
    SolicitudCobroResponseDTO toSolicitudCobroResponse(SolicitudCobro entity);

    // Mapeos de Deudas (DestinoSolicitudCobro)
    @Mapping(source = "solicitudCobro.solicitudCobroId", target = "solicitudCobroId")
    @Mapping(source = "solicitudCobro.titulo", target = "tituloCobro")
    @Mapping(source = "solicitudCobro.descripcion", target = "descripcionCobro")
    @Mapping(source = "solicitudCobro.monto", target = "montoOriginal")
    @Mapping(source = "solicitudCobro.moneda", target = "moneda")
    @Mapping(source = "solicitudCobro.expiraEl", target = "expiraEl")
    DeudaResponseDTO toDeudaResponse(DestinoSolicitudCobro entity);

    // Mapeos de Transacciones
    @Mapping(source = "solicitudCobro.solicitudCobroId", target = "solicitudCobroId")
    TransaccionResponseDTO toTransaccionResponse(TransaccionPago entity);

    // Mapeos de Reembolsos
    @Mapping(source = "transaccionPago.transaccionPagoId", target = "transaccionPagoId")
    ReembolsoResponseDTO toReembolsoResponse(Reembolso entity);
}