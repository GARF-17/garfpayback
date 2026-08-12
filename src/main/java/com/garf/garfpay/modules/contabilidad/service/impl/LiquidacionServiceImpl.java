package com.garf.garfpay.modules.contabilidad.service.impl;

import com.garf.garfpay.modules.contabilidad.dto.response.LiquidacionResponseDTO;
import com.garf.garfpay.modules.contabilidad.entity.Liquidacion;
import com.garf.garfpay.modules.contabilidad.entity.LiquidacionDetalle;
import com.garf.garfpay.modules.contabilidad.entity.LiquidacionDetalleId;
import com.garf.garfpay.modules.contabilidad.enums.EstadoLiquidacion;
import com.garf.garfpay.modules.contabilidad.mapper.ContabilidadMapper;
import com.garf.garfpay.modules.contabilidad.repository.LiquidacionDetalleRepository;
import com.garf.garfpay.modules.contabilidad.repository.LiquidacionRepository;
import com.garf.garfpay.modules.contabilidad.service.ILiquidacionService;
import com.garf.garfpay.modules.pagos.entity.TransaccionPago;
import com.garf.garfpay.modules.pagos.repository.TransaccionPagoRepository;
import com.garf.garfpay.modules.tenant.entity.CuentaLiquidacion;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.repository.CuentaLiquidacionRepository;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import com.garf.garfpay.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidacionServiceImpl implements ILiquidacionService {

    private final LiquidacionRepository liquidacionRepository;
    private final LiquidacionDetalleRepository liquidacionDetalleRepository;
    private final TransaccionPagoRepository transaccionPagoRepository;
    private final OrganizacionRepository organizacionRepository;
    private final CuentaLiquidacionRepository cuentaLiquidacionRepository;
    private final ContabilidadMapper contabilidadMapper;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public LiquidacionResponseDTO generarLiquidacion(UUID organizacionId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Iniciando cálculo de liquidación para Org: {} entre {} y {}", organizacionId, fechaInicio, fechaFin);

        if (fechaFin.isBefore(fechaInicio)) {
            throw new BusinessRuleException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada."));

        CuentaLiquidacion cuentaPrincipal = cuentaLiquidacionRepository
                .findByOrganizacionOrganizacionIdAndEsPrincipalTrueAndEstaActivaTrue(organizacionId)
                .orElseThrow(() -> new BusinessRuleException("La organización no cuenta con una cuenta de liquidación (CCI) principal y activa."));

        OffsetDateTime inicio = fechaInicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime fin = fechaFin.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).minusNanos(1);

        List<TransaccionPago> transacciones = transaccionPagoRepository
                .buscarTransaccionesNoLiquidadas(organizacionId, inicio, fin);

        if (transacciones.isEmpty()) {
            throw new BusinessRuleException("No existen transacciones pendientes de liquidar en el período especificado.");
        }

        BigDecimal montoBruto = transacciones.stream()
                .map(TransaccionPago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComisionesPasarela = transacciones.stream()
                .map(TransaccionPago::getComisionPasarela)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComisionesPlataforma = transacciones.stream()
                .map(TransaccionPago::getComisionPlataforma)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montoComisiones = totalComisionesPasarela.add(totalComisionesPlataforma);

        Liquidacion liquidacion = Liquidacion.builder()
                .organizacion(organizacion)
                .cuentaLiquidacion(cuentaPrincipal)
                .periodoInicio(fechaInicio)
                .periodoFin(fechaFin)
                .montoBruto(montoBruto)
                .montoComisiones(montoComisiones)
                .estado(EstadoLiquidacion.PENDIENTE)
                .build();

        liquidacion = liquidacionRepository.save(liquidacion);

        for (TransaccionPago tx : transacciones) {
            LiquidacionDetalle detalle = LiquidacionDetalle.builder()
                    .id(new LiquidacionDetalleId(liquidacion.getLiquidacionId(), tx.getTransaccionPagoId()))
                    .liquidacion(liquidacion)
                    .transaccionPago(tx)
                    .build();
            liquidacionDetalleRepository.save(detalle);
        }

        log.info("Liquidación {} creada con éxito. Monto Bruto: PEN {}", liquidacion.getLiquidacionId(), montoBruto);
        return contabilidadMapper.toLiquidacionResponse(liquidacion);
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO confirmarTransferenciaBancaria(UUID liquidacionId, String referenciaTransferencia) {
        Liquidacion liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada."));

        if (liquidacion.getEstado() == EstadoLiquidacion.COMPLETADA) {
            throw new BusinessRuleException("La liquidación ya fue completada previamente.");
        }

        liquidacion.setEstado(EstadoLiquidacion.COMPLETADA);
        liquidacion.setReferenciaTransferencia(referenciaTransferencia);
        liquidacion.setLiquidadoEl(OffsetDateTime.now());

        return contabilidadMapper.toLiquidacionResponse(liquidacionRepository.save(liquidacion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiquidacionResponseDTO> listarLiquidacionesPorOrganizacion(UUID organizacionId) {
        return liquidacionRepository.findByOrganizacion_OrganizacionId(organizacionId).stream()
                .map(contabilidadMapper::toLiquidacionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LiquidacionResponseDTO obtenerLiquidacionPorId(UUID liquidacionId) {
        Liquidacion liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada."));
        return contabilidadMapper.toLiquidacionResponse(liquidacion);
    }
}