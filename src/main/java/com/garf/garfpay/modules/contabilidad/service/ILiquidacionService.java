package com.garf.garfpay.modules.contabilidad.service;

import com.garf.garfpay.modules.contabilidad.dto.response.LiquidacionResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ILiquidacionService {
    LiquidacionResponseDTO generarLiquidacion(UUID organizacionId, LocalDate fechaInicio, LocalDate fechaFin);
    LiquidacionResponseDTO confirmarTransferenciaBancaria(UUID liquidacionId, String referenciaTransferencia);
    List<LiquidacionResponseDTO> listarLiquidacionesPorOrganizacion(UUID organizacionId);
    LiquidacionResponseDTO obtenerLiquidacionPorId(UUID liquidacionId);
}