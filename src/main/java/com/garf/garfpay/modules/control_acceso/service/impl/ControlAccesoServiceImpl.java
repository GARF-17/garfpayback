package com.garf.garfpay.modules.control_acceso.service.impl;

import com.garf.garfpay.modules.control_acceso.dto.request.CrearRolRequestDTO;
import com.garf.garfpay.modules.control_acceso.dto.response.PermisoResponseDTO;
import com.garf.garfpay.modules.control_acceso.dto.response.RolResponseDTO;
import com.garf.garfpay.modules.control_acceso.entity.Permiso;
import com.garf.garfpay.modules.control_acceso.entity.Rol;
import com.garf.garfpay.modules.control_acceso.mapper.ControlAccesoMapper;
import com.garf.garfpay.modules.control_acceso.repository.PermisoRepository;
import com.garf.garfpay.modules.control_acceso.repository.RolRepository;
import com.garf.garfpay.modules.control_acceso.service.IControlAccesoService;
import com.garf.garfpay.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ControlAccesoServiceImpl implements IControlAccesoService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final ControlAccesoMapper mapper;

    @Override
    @Transactional
    public RolResponseDTO crearRol(CrearRolRequestDTO request) {
        // 1. Validar que el código no exista
        if (rolRepository.existsByCodigo(request.codigo())) {
            throw new ConflictException("Ya existe un rol con el código: " + request.codigo());
        }

        // 2. Buscar los permisos que se le van a asignar (si envían IDs)
        Set<Permiso> permisosAsignados = new HashSet<>();
        if (request.permisoIds() != null && !request.permisoIds().isEmpty()) {
            permisosAsignados = permisoRepository.findByPermisoIdIn(request.permisoIds());
        }

        // 3. Crear Entidad
        Rol nuevoRol = Rol.builder()
                .codigo(request.codigo())
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .ambito(request.ambito())
                .esSistema(false) //Los roles creados por API NUNCA son de sistema
                .permisos(permisosAsignados)
                .build();

        Rol rolGuardado = rolRepository.save(nuevoRol);

        // 4. Mapear y retornar
        return mapper.toRolResponse(rolGuardado);
    }

    @Override
    @Transactional(readOnly = true) // Mejora el rendimiento en lecturas
    public List<RolResponseDTO> listarRoles() {
        return rolRepository.findAllWithPermisos()
                .stream()
                .map(mapper::toRolResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponseDTO> listarPermisos() {
        return permisoRepository.findAll()
                .stream()
                .map(mapper::toPermisoResponse)
                .collect(Collectors.toList());
    }
}