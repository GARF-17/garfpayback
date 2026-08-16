package com.garf.garfpay.modules.pagos.service.impl;

import com.garf.garfpay.modules.pagos.dto.request.RegistrarMetodoPagoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.MetodoPagoResponseDTO;
import com.garf.garfpay.modules.pagos.entity.MetodoPagoGuardado;
import com.garf.garfpay.modules.pagos.repository.MetodoPagoGuardadoRepository;
import com.garf.garfpay.modules.pagos.service.IMetodoPagoService;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import com.garf.garfpay.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MetodoPagoServiceImpl implements IMetodoPagoService {

    private final MetodoPagoGuardadoRepository metodoPagoRepository;
    private final OrganizacionRepository organizacionRepository;

    @Override
    @Transactional
    public MetodoPagoResponseDTO registrarMetodoPago(UUID organizacionId, RegistrarMetodoPagoRequestDTO request) {
        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada."));

        // El nuevo método pasa a ser el predeterminado; se degrada el anterior.
        // CORRECCIÓN: Usamos saveAndFlush para obligar a Hibernate a hacer el UPDATE
        // en la BD de inmediato, liberando así la restricción única (Unique Constraint).
        metodoPagoRepository.findByOrganizacion_OrganizacionIdAndEsPredeterminadoTrueAndEstaActivoTrue(organizacionId)
                .ifPresent(actual -> {
                    actual.setEsPredeterminado(false);
                    metodoPagoRepository.saveAndFlush(actual); // <-- El truco de magia está aquí
                });

        MetodoPagoGuardado metodo = MetodoPagoGuardado.builder()
                .organizacion(organizacion)
                .proveedor(request.proveedor())
                .tokenProveedor(request.tokenProveedor())
                .marcaTarjeta(request.marcaTarjeta())
                .ultimosCuatroDigitos(request.ultimosCuatroDigitos())
                .esPredeterminado(true)
                .build();

        metodo = metodoPagoRepository.save(metodo);
        return toResponse(metodo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagoResponseDTO> listarMetodosPago(UUID organizacionId) {
        return metodoPagoRepository.findByOrganizacion_OrganizacionIdAndEstaActivoTrue(organizacionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void eliminarMetodoPago(UUID organizacionId, UUID metodoPagoId) {
        MetodoPagoGuardado metodo = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado."));

        if (!metodo.getOrganizacion().getOrganizacionId().equals(organizacionId)) {
            throw new BusinessRuleException("El método de pago no pertenece a esta organización.");
        }

        metodo.setEstaActivo(false);
        metodo.setEliminadoEl(OffsetDateTime.now());
        metodoPagoRepository.save(metodo);
    }

    private MetodoPagoResponseDTO toResponse(MetodoPagoGuardado m) {
        return new MetodoPagoResponseDTO(
                m.getMetodoPagoId(), m.getOrganizacion().getOrganizacionId(),
                m.getProveedor().name(), m.getMarcaTarjeta(), m.getUltimosCuatroDigitos(),
                m.getEsPredeterminado(), m.getCreadoEl());
    }
}