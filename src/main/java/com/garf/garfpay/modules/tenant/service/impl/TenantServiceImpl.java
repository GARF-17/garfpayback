package com.garf.garfpay.modules.tenant.service.impl;

import com.garf.garfpay.modules.control_acceso.entity.Rol;
import com.garf.garfpay.modules.control_acceso.repository.RolRepository;
import com.garf.garfpay.modules.facturacion.service.IFacturacionService;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.tenant.dto.request.CrearCuentaLiquidacionRequestDTO;
import com.garf.garfpay.modules.tenant.dto.request.CrearOrganizacionRequestDTO;
import com.garf.garfpay.modules.tenant.dto.response.CuentaLiquidacionResponseDTO;
import com.garf.garfpay.modules.tenant.dto.response.OrganizacionResponseDTO;
import com.garf.garfpay.modules.tenant.entity.*;
import com.garf.garfpay.modules.tenant.enums.EstadoOrganizacion;
import com.garf.garfpay.modules.tenant.enums.TipoOrganizacion;
import com.garf.garfpay.modules.tenant.mapper.TenantMapper;
import com.garf.garfpay.modules.tenant.repository.AuditoriaCuentaLiquidacionRepository;
import com.garf.garfpay.modules.tenant.repository.CuentaLiquidacionRepository;
import com.garf.garfpay.modules.tenant.repository.MiembroOrganizacionRepository;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import com.garf.garfpay.modules.tenant.service.ITenantService;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import com.garf.garfpay.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements ITenantService {

    private final OrganizacionRepository organizacionRepository;
    private final MiembroOrganizacionRepository miembroOrganizacionRepository;
    private final CuentaLiquidacionRepository cuentaLiquidacionRepository;
    private final AuditoriaCuentaLiquidacionRepository auditoriaRepository;
    private final UsuarioAppRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TenantMapper tenantMapper;
    private final IFacturacionService facturacionService;

    @Override
    @Transactional
    public OrganizacionResponseDTO crearOrganizacion(CrearOrganizacionRequestDTO request, String nombreUsuarioCreador) {

        if (request.documentoIdentidad() != null && !request.documentoIdentidad().isBlank()) {
            if (organizacionRepository.existsByDocumentoIdentidad(request.documentoIdentidad())) {
                throw new ConflictException("El documento de identidad ya está registrado en otra organización.");
            }
        }

        UsuarioApp usuarioCreador = usuarioRepository.findByNombreUsuario(nombreUsuarioCreador)
                .orElseThrow(() -> new BusinessRuleException("Usuario creador no encontrado."));

        Organizacion organizacion = tenantMapper.toOrganizacionEntity(request);

        // Toda Organización nace SUSPENDIDA Pendiente de revisión
        organizacion.setEstado(EstadoOrganizacion.SUSPENDIDA);

        organizacion = organizacionRepository.save(organizacion);

        String codigoRol = (request.tipoOrganizacion() == TipoOrganizacion.PERSONAL) ? "USER" : "ORG_ADMIN";
        Rol rolAsignado = rolRepository.findByCodigo(codigoRol)
                .orElseThrow(() -> new BusinessRuleException("Error crítico: Rol no existe."));

        MiembroOrganizacion miembro = MiembroOrganizacion.builder()
                .id(new MiembroOrganizacionId(organizacion.getOrganizacionId(), usuarioCreador.getUsuarioId()))
                .organizacion(organizacion)
                .usuario(usuarioCreador)
                .rol(rolAsignado)
                .build();

        miembroOrganizacionRepository.save(miembro);

        return tenantMapper.toOrganizacionResponse(organizacion);
    }

    @Override
    @Transactional
    public CuentaLiquidacionResponseDTO agregarCuentaLiquidacion(UUID organizacionId, CrearCuentaLiquidacionRequestDTO request, String nombreUsuarioAuditor, String ipAddress) {

        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new BusinessRuleException("La organización no existe."));

        // Bloquear si no está aprobada
        if (organizacion.getEstado() != EstadoOrganizacion.ACTIVA) {
            throw new BusinessRuleException("La organización está en revisión (SUSPENDIDA). No puede registrar cuentas bancarias ni operar hasta que GarfPay apruebe su solicitud.");
        }

        UsuarioApp usuarioAuditor = usuarioRepository.findByNombreUsuario(nombreUsuarioAuditor)
                .orElseThrow(() -> new BusinessRuleException("Usuario auditor no encontrado."));

        boolean esMiembro = miembroOrganizacionRepository.existsById(
                new MiembroOrganizacionId(organizacionId, usuarioAuditor.getUsuarioId())
        );

        if (!esMiembro && !tieneRolGlobal(usuarioAuditor, "SUPER_ADMIN")) {
            throw new BusinessRuleException("No perteneces a esta organización. Acción denegada.");
        }

        CuentaLiquidacion cuenta = tenantMapper.toCuentaLiquidacionEntity(request);
        cuenta.setOrganizacion(organizacion);
        cuenta = cuentaLiquidacionRepository.save(cuenta);

        AuditoriaCuentaLiquidacion auditoria = AuditoriaCuentaLiquidacion.builder()
                .cuentaLiquidacion(cuenta)
                .cambiadoPor(usuarioAuditor)
                .cciNuevo(cuenta.getCci())
                .direccionIp(ipAddress)
                .build();
        auditoriaRepository.save(auditoria);

        return tenantMapper.toCuentaLiquidacionResponse(cuenta);
    }

    // Solo el Administrador usará esto
    @Override
    @Transactional
    public OrganizacionResponseDTO activarOrganizacion(UUID organizacionId) {
        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new BusinessRuleException("La organización no existe."));

        organizacion.setEstado(EstadoOrganizacion.ACTIVA);
        organizacion = organizacionRepository.save(organizacion);

        facturacionService.asignarSuscripcionAutomatica(organizacion.getOrganizacionId());

        return tenantMapper.toOrganizacionResponse(organizacion);
    }

    private boolean tieneRolGlobal(UsuarioApp usuario, String codigoRolGlobal) {
        return usuario.getRoles().stream()
                .anyMatch(ur -> ur.getRol().getCodigo().equals(codigoRolGlobal));
    }
}