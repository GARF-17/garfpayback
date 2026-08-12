package com.garf.garfpay.modules.pagos.service.impl;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.pagos.dto.request.CrearSolicitudCobroRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.DeudaResponseDTO;
import com.garf.garfpay.modules.pagos.dto.response.SolicitudCobroResponseDTO;
import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobro;
import com.garf.garfpay.modules.pagos.entity.DestinoSolicitudCobroId;
import com.garf.garfpay.modules.pagos.entity.SolicitudCobro;
import com.garf.garfpay.modules.pagos.enums.EstadoDestinoCobro;
import com.garf.garfpay.modules.pagos.mapper.PagosMapper;
import com.garf.garfpay.modules.pagos.repository.DestinoSolicitudCobroRepository;
import com.garf.garfpay.modules.pagos.repository.SolicitudCobroRepository;
import com.garf.garfpay.modules.pagos.service.ISolicitudCobroService;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.enums.EstadoOrganizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolicitudCobroServiceImpl implements ISolicitudCobroService {

    private final SolicitudCobroRepository solicitudCobroRepository;
    private final DestinoSolicitudCobroRepository destinoRepository;
    private final OrganizacionRepository organizacionRepository;
    private final UsuarioAppRepository usuarioRepository;
    private final PagosMapper pagosMapper;

    @Override
    @Transactional
    public SolicitudCobroResponseDTO crearSolicitudCobro(UUID organizacionId, CrearSolicitudCobroRequestDTO request, String nombreUsuarioCreador) {

        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new BusinessRuleException("Organización no encontrada."));

        // Si la empresa está suspendida, no puede generar cobros
        if (organizacion.getEstado() != EstadoOrganizacion.ACTIVA) {
            throw new BusinessRuleException("La organización no está activa. No puede generar solicitudes de cobro.");
        }

        UsuarioApp usuarioCreador = usuarioRepository.findByNombreUsuario(nombreUsuarioCreador)
                .orElseThrow(() -> new BusinessRuleException("Usuario creador no encontrado."));

        // Guardar la solicitud
        SolicitudCobro solicitud = pagosMapper.toSolicitudCobroEntity(request);
        solicitud.setOrganizacion(organizacion);
        solicitud.setCreadoPor(usuarioCreador);
        solicitud = solicitudCobroRepository.save(solicitud);

        // Asignar la deuda a cada usuario destino (Detalles)
        for (UUID destinoId : request.usuariosDestinoIds()) {
            UsuarioApp alumno = usuarioRepository.findById(destinoId)
                    .orElseThrow(() -> new BusinessRuleException("El usuario destino con ID " + destinoId + " no existe."));

            DestinoSolicitudCobro destino = DestinoSolicitudCobro.builder()
                    .id(new DestinoSolicitudCobroId(solicitud.getSolicitudCobroId(), alumno.getUsuarioId()))
                    .solicitudCobro(solicitud)
                    .usuario(alumno)
                    .estado(EstadoDestinoCobro.PENDIENTE)
                    .build();

            destinoRepository.save(destino);
        }

        return pagosMapper.toSolicitudCobroResponse(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeudaResponseDTO> listarDeudasPorUsuario(UUID usuarioId) {
        return destinoRepository.findByUsuario_UsuarioId(usuarioId).stream()
                .map(pagosMapper::toDeudaResponse)
                .toList();
    }
}