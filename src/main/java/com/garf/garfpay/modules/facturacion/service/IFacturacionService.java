package com.garf.garfpay.modules.facturacion.service;

import com.garf.garfpay.modules.facturacion.dto.request.CrearPlanRequestDTO;
import com.garf.garfpay.modules.facturacion.dto.request.SuscribirOrganizacionRequestDTO;
import com.garf.garfpay.modules.facturacion.dto.response.PlanSuscripcionResponseDTO;
import com.garf.garfpay.modules.facturacion.dto.response.SuscripcionOrganizacionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IFacturacionService {
    SuscripcionOrganizacionResponseDTO asignarSuscripcionAutomatica(UUID organizacionId);
    List<PlanSuscripcionResponseDTO> listarPlanesActivos();
    SuscripcionOrganizacionResponseDTO obtenerSuscripcionActiva(UUID organizacionId);
    PlanSuscripcionResponseDTO crearPlan(CrearPlanRequestDTO request);
    SuscripcionOrganizacionResponseDTO asignarSuscripcionManual(SuscribirOrganizacionRequestDTO request);
}