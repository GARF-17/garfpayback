package com.garf.garfpay.modules.contabilidad.service;

import com.garf.garfpay.modules.contabilidad.dto.request.CrearTarifarioRequestDTO;
import com.garf.garfpay.modules.contabilidad.dto.response.TarifarioResponseDTO;
import com.garf.garfpay.modules.pagos.enums.NombreProveedor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ITarifarioService {
    TarifarioResponseDTO crearTarifario(CrearTarifarioRequestDTO request);
    List<TarifarioResponseDTO> listarTarifariosPorOrganizacion(UUID organizacionId);
    TarifarioResponseDTO obtenerTarifarioVigente(UUID organizacionId, NombreProveedor proveedor, OffsetDateTime fecha);
    BigDecimal calcularComision(UUID organizacionId, NombreProveedor proveedor, BigDecimal monto, OffsetDateTime fecha);
}