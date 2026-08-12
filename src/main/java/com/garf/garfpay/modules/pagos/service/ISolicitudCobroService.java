package com.garf.garfpay.modules.pagos.service;

import com.garf.garfpay.modules.pagos.dto.request.CrearSolicitudCobroRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.DeudaResponseDTO;
import com.garf.garfpay.modules.pagos.dto.response.SolicitudCobroResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ISolicitudCobroService {
    SolicitudCobroResponseDTO crearSolicitudCobro(UUID organizacionId, CrearSolicitudCobroRequestDTO request, String nombreUsuarioCreador);
    List<DeudaResponseDTO> listarDeudasPorUsuario(UUID usuarioId);
}