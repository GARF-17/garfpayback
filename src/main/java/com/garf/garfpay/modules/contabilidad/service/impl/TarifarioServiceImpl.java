package com.garf.garfpay.modules.contabilidad.service.impl;

import com.garf.garfpay.modules.contabilidad.dto.request.CrearTarifarioRequestDTO;
import com.garf.garfpay.modules.contabilidad.dto.response.TarifarioResponseDTO;
import com.garf.garfpay.modules.contabilidad.entity.Tarifario;
import com.garf.garfpay.modules.contabilidad.mapper.ContabilidadMapper;
import com.garf.garfpay.modules.contabilidad.repository.TarifarioRepository;
import com.garf.garfpay.modules.contabilidad.service.ITarifarioService;
import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import com.garf.garfpay.shared.exception.ResourceNotFoundException;
import com.garf.garfpay.shared.util.MoneyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TarifarioServiceImpl implements ITarifarioService {

    private final TarifarioRepository tarifarioRepository;
    private final OrganizacionRepository organizacionRepository;
    private final ContabilidadMapper mapper;

    @Override
    @Transactional
    public TarifarioResponseDTO crearTarifario(CrearTarifarioRequestDTO request) {
        log.info("Creando tarifario para proveedor: {}", request.proveedor());

        if (request.vigenteHasta() != null && request.vigenteHasta().isBefore(request.vigenteDesde())) {
            throw new BusinessRuleException("La fecha 'vigenteHasta' debe ser posterior a 'vigenteDesde'.");
        }

        Tarifario tarifario = mapper.toTarifarioEntity(request);

        if (request.organizacionId() != null) {
            Organizacion org = organizacionRepository.findById(request.organizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada."));
            tarifario.setOrganizacion(org);
        }

        tarifario = tarifarioRepository.save(tarifario);
        log.info("Tarifario creado exitosamente con ID: {}", tarifario.getTarifarioId());
        return mapper.toTarifarioResponse(tarifario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarifarioResponseDTO> listarTarifariosPorOrganizacion(UUID organizacionId) {
        return tarifarioRepository.findByOrganizacion_OrganizacionId(organizacionId).stream()
                .map(mapper::toTarifarioResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TarifarioResponseDTO obtenerTarifarioVigente(UUID organizacionId, NombreProveedor proveedor, OffsetDateTime fecha) {
        Tarifario tarifario = buscarTarifarioAplicable(organizacionId, proveedor, fecha);
        return mapper.toTarifarioResponse(tarifario);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularComision(UUID organizacionId, NombreProveedor proveedor, BigDecimal monto, OffsetDateTime fecha) {
        Tarifario tarifario = buscarTarifarioAplicable(organizacionId, proveedor, fecha);

        BigDecimal comisionVariable = monto.multiply(tarifario.getComisionPorcentaje());
        BigDecimal comisionTotal = comisionVariable.add(tarifario.getComisionFija());

        BigDecimal comisionFormateada = MoneyUtils.formatAmount(comisionTotal);

        log.debug("Comisión calculada para org={} proveedor={} monto={} => comision={} (tarifario={})",
                organizacionId, proveedor, monto, comisionFormateada, tarifario.getTarifarioId());

        return comisionFormateada;
    }

    private Tarifario buscarTarifarioAplicable(UUID organizacionId, NombreProveedor proveedor, OffsetDateTime fecha) {
        List<Tarifario> tarifarios = tarifarioRepository.buscarTarifariosVigentes(organizacionId, proveedor, fecha);

        if (tarifarios.isEmpty()) {
            throw new BusinessRuleException(
                    "No existe un tarifario vigente para el proveedor " + proveedor +
                            ". No es posible procesar la transacción sin una tarifa configurada.");
        }

        return tarifarios.get(0);
    }
}