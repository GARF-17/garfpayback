package com.garf.garfpay.modules.contabilidad.mapper;

import com.garf.garfpay.modules.contabilidad.dto.request.CrearTarifarioRequestDTO;
import com.garf.garfpay.modules.contabilidad.dto.response.EventoTransaccionResponseDTO;
import com.garf.garfpay.modules.contabilidad.dto.response.LiquidacionResponseDTO;
import com.garf.garfpay.modules.contabilidad.dto.response.TarifarioResponseDTO;
import com.garf.garfpay.modules.contabilidad.entity.EventoTransaccion;
import com.garf.garfpay.modules.contabilidad.entity.Liquidacion;
import com.garf.garfpay.modules.contabilidad.entity.Tarifario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContabilidadMapper {

    @Mapping(source = "organizacion.organizacionId", target = "organizacionId")
    TarifarioResponseDTO toTarifarioResponse(Tarifario entity);

    Tarifario toTarifarioEntity(CrearTarifarioRequestDTO dto);

    @Mapping(source = "organizacion.organizacionId", target = "organizacionId")
    @Mapping(source = "cuentaLiquidacion.nombreBanco", target = "bancoDestino")
    @Mapping(source = "cuentaLiquidacion.cci", target = "cciDestino")
    @Mapping(source = "cuentaLiquidacion.titularCuenta", target = "titularCuenta")
    LiquidacionResponseDTO toLiquidacionResponse(Liquidacion entity);

    @Mapping(source = "transaccionPago.transaccionPagoId", target = "transaccionPagoId")
    EventoTransaccionResponseDTO toEventoResponse(EventoTransaccion entity);
}