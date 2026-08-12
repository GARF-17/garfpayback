package com.garf.garfpay.modules.facturacion.service.impl;

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
import com.garf.garfpay.modules.tenant.enums.CategoriaOrganizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacturacionServiceImpl implements IFacturacionService {

    private final SuscripcionOrganizacionRepository suscripcionRepository;
    private final PlanSuscripcionRepository planRepository;
    private final OrganizacionRepository organizacionRepository;
    private final FacturacionMapper facturacionMapper;

    @Override
    @Transactional
    public SuscripcionOrganizacionResponseDTO asignarSuscripcionAutomatica(UUID organizacionId) {

        // 1. Buscamos la organización recién activada
        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new BusinessRuleException("Organización no encontrada."));

        PlanSuscripcion planAsignado;
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin;

        // 2. MOTOR DE REGLAS DE FACTURACIÓN (Basado en la Categoría)
        CategoriaOrganizacion categoria = organizacion.getCategoria();

        if (categoria == CategoriaOrganizacion.ASOCIACION) {
            //PLAN ANUAL (Baja Frecuencia) -> 9.90 USD
            planAsignado = obtenerPlan(FrecuenciaSuscripcion.ANUAL);
            fechaFin = fechaInicio.plusYears(1); // El pago es al inicio, dura 1 año

        } else if (categoria == CategoriaOrganizacion.OTRO) {
            // EVALUACIÓN MANUAL (OTRO)
            throw new BusinessRuleException("La categoría 'OTRO' requiere que un administrador asigne el plan de facturación de forma manual.");

        } else {
            // PLAN MENSUAL (Alta Frecuencia) -> 3.90 USD
            // Categorías: COLEGIO, UNIVERSIDAD, EMPRESA_PRIVADA, TIPSTER_DEPORTIVO, EMPRENDEDOR, etc.
            planAsignado = obtenerPlan(FrecuenciaSuscripcion.MENSUAL);

            // REGLA DE NEGOCIO: 3 DÍAS DE PRUEBA GRATIS
            // La suscripción dura 3 días. Al expirar, nuestro Job (Cron) les cobrará los 3.90 USD para extenderla 1 mes.
            fechaFin = fechaInicio.plusDays(3);
        }

        // 3. Crear y guardar la Suscripción
        SuscripcionOrganizacion nuevaSuscripcion = SuscripcionOrganizacion.builder()
                .organizacion(organizacion)
                .planSuscripcion(planAsignado)
                .iniciaEl(fechaInicio)
                .terminaEl(fechaFin)
                .estaActiva(true)
                .build();

        nuevaSuscripcion = suscripcionRepository.save(nuevaSuscripcion);

        return facturacionMapper.toSuscripcionResponse(nuevaSuscripcion);
    }

    // Método auxiliar para buscar el plan en BD de forma segura
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
}