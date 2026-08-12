package com.garf.garfpay.modules.facturacion.seeder;

import com.garf.garfpay.modules.facturacion.entity.PlanSuscripcion;
import com.garf.garfpay.modules.facturacion.enums.FrecuenciaSuscripcion;
import com.garf.garfpay.modules.facturacion.repository.PlanSuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(3)
@RequiredArgsConstructor
public class PlanSuscripcionSeeder implements CommandLineRunner {

    private final PlanSuscripcionRepository planRepository;

    @Override
    public void run(String... args) {
        if (planRepository.count() == 0) {
            PlanSuscripcion anual = PlanSuscripcion.builder()
                    .nombre("Plan Anual Básico")
                    .descripcion("Ideal para asociaciones y colegios estatales con bajo volumen.")
                    .precio(new BigDecimal("9.90"))
                    .frecuencia(FrecuenciaSuscripcion.ANUAL)
                    .build();

            PlanSuscripcion mensual = PlanSuscripcion.builder()
                    .nombre("Plan Mensual Pro")
                    .descripcion("Para comunidades, tipsters y empresas. Incluye 3 días de prueba gratis.")
                    .precio(new BigDecimal("3.90"))
                    .frecuencia(FrecuenciaSuscripcion.MENSUAL)
                    .build();

            planRepository.save(anual);
            planRepository.save(mensual);
            System.out.println(" Planes de Facturación creados exitosamente.");
        }
    }
}