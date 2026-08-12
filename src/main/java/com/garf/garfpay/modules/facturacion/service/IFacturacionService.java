package com.garf.garfpay.modules.facturacion.service;

import com.garf.garfpay.modules.facturacion.dto.response.PlanSuscripcionResponseDTO;
import com.garf.garfpay.modules.facturacion.dto.response.SuscripcionOrganizacionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IFacturacionService {

    // Se llamará automáticamente cuando se active una organización
    SuscripcionOrganizacionResponseDTO asignarSuscripcionAutomatica(UUID organizacionId);
    List<PlanSuscripcionResponseDTO> listarPlanesActivos();
    SuscripcionOrganizacionResponseDTO obtenerSuscripcionActiva(UUID organizacionId);

}