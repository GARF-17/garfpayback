package com.garf.garfpay.modules.facturacion.service.impl;

import com.garf.garfpay.modules.facturacion.dto.request.CrearPlanRequestDTO;
import com.garf.garfpay.modules.facturacion.dto.request.SuscribirOrganizacionRequestDTO;
import com.garf.garfpay.modules.facturacion.dto.response.PlanSuscripcionResponseDTO;
import com.garf.garfpay.modules.facturacion.dto.response.SuscripcionOrganizacionResponseDTO;
import com.garf.garfpay.modules.facturacion.entity.PlanSuscripcion;
import com.garf.garfpay.modules.facturacion.entity.SuscripcionOrganizacion;
import com.garf.garfpay.modules.facturacion.enums.FrecuenciaSuscripcion;
import com.garf.garfpay.modules.facturacion.mapper.FacturacionMapper;
import com.garf.garfpay.modules.facturacion.repository.PlanSuscripcionRepository;
import com.garf.garfpay.modules.facturacion.repository.SuscripcionOrganizacionRepository;
import com.garf.garfpay.modules.facturacion.service.IFacturacionService;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // <-- CORRECCIÓN 1: Se usa Slf4j en lugar del import estático erróneo
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturacionServiceImpl implements IFacturacionService {

    private final SuscripcionOrganizacionRepository suscripcionRepository;
    private final PlanSuscripcionRepository planRepository;
    private final OrganizacionRepository organizacionRepository;
    private final FacturacionMapper facturacionMapper;

    // Inyectamos los días de prueba.
    @Value("${facturacion.dias-prueba-gratis:14}")
    private int diasPruebaGratis;

    @Override
    @Transactional
    public SuscripcionOrganizacionResponseDTO asignarSuscripcionAutomatica(UUID organizacionId) {

        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new BusinessRuleException("Organización no encontrada."));

        PlanSuscripcion planPorDefecto = planRepository.findByEsPlanPorDefectoTrueAndEstaActivoTrue()
                .orElseThrow(() -> new BusinessRuleException(
                        "No hay un plan de suscripción por defecto configurado. Contacte al administrador del sistema."));

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = calcularFechaFinPeriodoPrueba(planPorDefecto, fechaInicio);

        SuscripcionOrganizacion nuevaSuscripcion = SuscripcionOrganizacion.builder()
                .organizacion(organizacion)
                .planSuscripcion(planPorDefecto)
                .iniciaEl(fechaInicio)
                .terminaEl(fechaFin)
                .estaActiva(true)
                .build();

        nuevaSuscripcion = suscripcionRepository.save(nuevaSuscripcion);

        log.info("Suscripción automática asignada: organización={}, plan={}", organizacionId, planPorDefecto.getNombre());
        return facturacionMapper.toSuscripcionResponse(nuevaSuscripcion);
    }

    private LocalDate calcularFechaFinPeriodoPrueba(PlanSuscripcion plan, LocalDate fechaInicio) {
        return switch (plan.getFrecuencia()) {
            case MENSUAL -> fechaInicio.plusDays(diasPruebaGratis);
            case SEMANAL -> fechaInicio.plusDays(Math.min(diasPruebaGratis, 3));
            case QUINCENAL -> fechaInicio.plusDays(diasPruebaGratis);
            case ANUAL -> fechaInicio.plusYears(1);
        };
    }

    private PlanSuscripcion obtenerPlan(FrecuenciaSuscripcion frecuencia) {
        return planRepository.findByFrecuenciaAndEstaActivoTrue(frecuencia)
                .orElseThrow(() -> new BusinessRuleException("No se encontró un plan activo para la frecuencia: " + frecuencia));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanSuscripcionResponseDTO> listarPlanesActivos() {
        return planRepository.findAll().stream()
                .filter(PlanSuscripcion::getEstaActivo)
                .map(facturacionMapper::toPlanResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SuscripcionOrganizacionResponseDTO obtenerSuscripcionActiva(UUID organizacionId) {
        SuscripcionOrganizacion suscripcion = suscripcionRepository
                .buscarSuscripcionActivaPorOrganizacion(organizacionId)
                .orElseThrow(() -> new BusinessRuleException("La organización no tiene una suscripción activa."));

        return facturacionMapper.toSuscripcionResponse(suscripcion);
    }

    @Transactional
    public PlanSuscripcionResponseDTO crearPlan(CrearPlanRequestDTO request) {
        if (request.esPlanPorDefecto()) {
            planRepository.findByEsPlanPorDefectoTrueAndEstaActivoTrue()
                    .ifPresent(planActual -> {
                        planActual.setEstaActivo(false);
                        planRepository.save(planActual);
                    });
        }
        PlanSuscripcion plan = PlanSuscripcion.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .frecuencia(request.frecuencia())
                .esPlanPorDefecto(request.esPlanPorDefecto())
                .build();
        return facturacionMapper.toPlanResponse(planRepository.save(plan));
    }

    @Override
    @Transactional
    public SuscripcionOrganizacionResponseDTO asignarSuscripcionManual(SuscribirOrganizacionRequestDTO request) {
        Organizacion organizacion = organizacionRepository.findById(request.organizacionId())
                .orElseThrow(() -> new BusinessRuleException("Organización no encontrada."));
        PlanSuscripcion plan = planRepository.findById(request.planSuscripcionId())
                .orElseThrow(() -> new BusinessRuleException("Plan de suscripción no encontrado."));

        if (request.terminaEl().isBefore(request.iniciaEl())) {
            throw new BusinessRuleException("La fecha de término no puede ser anterior a la fecha de inicio.");
        }

        suscripcionRepository.buscarSuscripcionActivaPorOrganizacion(request.organizacionId())
                .ifPresent(activa -> { activa.setEstaActiva(false); suscripcionRepository.save(activa); });

        SuscripcionOrganizacion suscripcion = SuscripcionOrganizacion.builder()
                .organizacion(organizacion)
                .planSuscripcion(plan)
                .iniciaEl(request.iniciaEl())
                .terminaEl(request.terminaEl())
                .estaActiva(true)
                .build();

        return facturacionMapper.toSuscripcionResponse(suscripcionRepository.save(suscripcion));
    }
}