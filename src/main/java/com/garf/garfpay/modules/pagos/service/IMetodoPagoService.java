package com.garf.garfpay.modules.pagos.service;

import com.garf.garfpay.modules.pagos.dto.request.RegistrarMetodoPagoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.MetodoPagoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IMetodoPagoService {
    MetodoPagoResponseDTO registrarMetodoPago(UUID organizacionId, RegistrarMetodoPagoRequestDTO request);
    List<MetodoPagoResponseDTO> listarMetodosPago(UUID organizacionId);
    void eliminarMetodoPago(UUID organizacionId, UUID metodoPagoId);
}