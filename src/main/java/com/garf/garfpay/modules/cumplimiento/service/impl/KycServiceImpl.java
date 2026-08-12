package com.garf.garfpay.modules.cumplimiento.service.impl;

import com.garf.garfpay.modules.cumplimiento.dto.request.IniciarKycRequestDTO;
import com.garf.garfpay.modules.cumplimiento.dto.response.VerificacionKycResponseDTO;
import com.garf.garfpay.modules.cumplimiento.entity.VerificacionKyc;
import com.garf.garfpay.modules.cumplimiento.enums.EstadoKyc;
import com.garf.garfpay.modules.cumplimiento.mapper.CumplimientoMapper;
import com.garf.garfpay.modules.cumplimiento.repository.VerificacionKycRepository;
import com.garf.garfpay.modules.cumplimiento.service.IKycService;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import com.garf.garfpay.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KycServiceImpl implements IKycService {

    private final VerificacionKycRepository kycRepository;
    private final OrganizacionRepository organizacionRepository;
    private final CumplimientoMapper kycMapper;

    @Override
    @Transactional
    public VerificacionKycResponseDTO iniciarVerificacion(UUID organizacionId, IniciarKycRequestDTO request) {
        log.info("Iniciando KYC para organización {} con proveedor {}", organizacionId, request.nombreProveedor());

        Organizacion org = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada."));

        //AQUÍ IRÍA LA INTEGRACIÓN CON LA API EXTERNA (EJ: EQUIFAX / SUMSUB)
        // val respuestaExterna = proveedorKycClient.validarEmpresa(request.payloadSolicitud());

        VerificacionKyc verificacion = VerificacionKyc.builder()
                .organizacion(org)
                .nombreProveedor(request.nombreProveedor())
                .payloadSolicitud(request.payloadSolicitud())
                .estado(EstadoKyc.PENDIENTE) // Se queda pendiente hasta que el área legal o el webhook responda
                .build();

        verificacion = kycRepository.save(verificacion);
        return kycMapper.toResponse(verificacion);
    }

    @Override
    @Transactional
    public VerificacionKycResponseDTO actualizarEstadoKyc(UUID verificacionKycId, EstadoKyc nuevoEstado, Map<String, Object> payloadRespuesta) {
        VerificacionKyc verificacion = kycRepository.findById(verificacionKycId)
                .orElseThrow(() -> new ResourceNotFoundException("Verificación KYC no encontrada."));

        if (verificacion.getEstado() == EstadoKyc.APROBADO) {
            throw new BusinessRuleException("Esta verificación ya fue aprobada y no puede modificarse.");
        }

        verificacion.setEstado(nuevoEstado);

        // Guardamos la respuesta del proveedor (Ej: {"score_riesgo": 98, "lista_negra": false})
        if (payloadRespuesta != null && !payloadRespuesta.isEmpty()) {
            verificacion.setPayloadRespuesta(payloadRespuesta);
        }

        return kycMapper.toResponse(kycRepository.save(verificacion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VerificacionKycResponseDTO> listarVerificaciones(UUID organizacionId) {
        return kycRepository.findByOrganizacion_OrganizacionIdOrderByCreadoElDesc(organizacionId).stream()
                .map(kycMapper::toResponse)
                .toList();
    }
}