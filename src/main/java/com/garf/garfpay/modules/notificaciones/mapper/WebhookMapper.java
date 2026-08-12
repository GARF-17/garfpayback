package com.garf.garfpay.modules.notificaciones.mapper;

import com.garf.garfpay.modules.notificaciones.dto.request.CrearWebhookRequestDTO;
import com.garf.garfpay.modules.notificaciones.dto.response.EnvioWebhookResponseDTO;
import com.garf.garfpay.modules.notificaciones.dto.response.WebhookResponseDTO;
import com.garf.garfpay.modules.notificaciones.entity.EnvioWebhook;
import com.garf.garfpay.modules.notificaciones.entity.PuntoEnlaceWebhook;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WebhookMapper {

    PuntoEnlaceWebhook toWebhookEntity(CrearWebhookRequestDTO dto);

    @Mapping(source = "organizacion.organizacionId", target = "organizacionId")
    WebhookResponseDTO toWebhookResponse(PuntoEnlaceWebhook entity);

    EnvioWebhookResponseDTO toEnvioResponse(EnvioWebhook entity);
}