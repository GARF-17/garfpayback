package com.garf.garfpay.modules.pagos.service;

import com.garf.garfpay.modules.pagos.dto.request.ProcesarPagoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.TransaccionResponseDTO;

import java.util.UUID;

public interface ITransaccionPagoService {
    TransaccionResponseDTO procesarPago(UUID usuarioPagadorId, ProcesarPagoRequestDTO request);
}