package com.garf.garfpay.modules.auditoria.service.impl;

import com.garf.garfpay.modules.auditoria.dto.response.RegistroAuditoriaResponseDTO;
import com.garf.garfpay.modules.auditoria.entity.RegistroAuditoria;
import com.garf.garfpay.modules.auditoria.mapper.AuditoriaMapper;
import com.garf.garfpay.modules.auditoria.repository.RegistroAuditoriaRepository;
import com.garf.garfpay.modules.auditoria.service.IAuditoriaService;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.shared.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j @Service @RequiredArgsConstructor
public class AuditoriaServiceImpl implements IAuditoriaService {

    private final RegistroAuditoriaRepository auditoriaRepository;
    private final UsuarioAppRepository usuarioRepository;
    private final AuditoriaMapper auditoriaMapper;

    @Override
    @Async // La auditoría corre en un hilo separado para no ralentizar la aplicación
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Crea una transacción independiente
    public void registrarAccionInterna(UUID usuarioId, String nombreModulo, String nombreAccion,
                                       String nombreEntidad, UUID idEntidad,
                                       Map<String, Object> valoresAnteriores, Map<String, Object> valoresNuevos,
                                       String ip, String agenteUsuario) {

        try {
            UsuarioApp usuario = null;
            if (usuarioId != null) {
                usuario = usuarioRepository.findById(usuarioId).orElse(null);
            }

            RegistroAuditoria auditoria = RegistroAuditoria.builder()
                    .usuario(usuario)
                    .nombreModulo(nombreModulo)
                    .nombreAccion(nombreAccion)
                    .nombreEntidad(nombreEntidad)
                    .idEntidad(idEntidad)
                    .valoresAnteriores(valoresAnteriores)
                    .valoresNuevos(valoresNuevos)
                    .direccionIp(ip)
                    .agenteUsuario(agenteUsuario)
                    .build();

            auditoriaRepository.save(auditoria);
            log.info("Auditoría guardada: [{}] en {} (ID: {})", nombreAccion, nombreEntidad, idEntidad);
        } catch (Exception e) {
            // Un fallo en la auditoría no debe romper el sistema principal, solo lo logeamos
            log.error("Error crítico al guardar registro de auditoría", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RegistroAuditoriaResponseDTO> listarAuditoriasPorEntidad(UUID idEntidad, int page, int size) {
        Page<RegistroAuditoria> pagina = auditoriaRepository.findByIdEntidadOrderByCreadoElDesc(idEntidad, PageRequest.of(page, size));
        return new PageResponse<>(
                pagina.getContent().stream().map(auditoriaMapper::toResponse).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RegistroAuditoriaResponseDTO> listarAuditoriasPorUsuario(UUID usuarioId, int page, int size) {
        Page<RegistroAuditoria> pagina = auditoriaRepository.findByUsuario_UsuarioIdOrderByCreadoElDesc(usuarioId, PageRequest.of(page, size));
        return new PageResponse<>(
                pagina.getContent().stream().map(auditoriaMapper::toResponse).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isLast()
        );
    }
}