package com.garf.garfpay.shared.security;

import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.tenant.entity.MiembroOrganizacion;
import com.garf.garfpay.modules.tenant.repository.MiembroOrganizacionRepository;
import com.garf.garfpay.shared.constants.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Componente central de aislamiento multi-tenant. Se invoca desde @PreAuthorize
 * en los controllers para verificar que el usuario autenticado realmente pertenece
 * a la organización que está intentando operar — antes solo se validaba el rol
 * global, sin cruzar contra la membresía real.
 *
 * Excepción: SUPER_ADMIN (rol de plataforma) puede operar sobre cualquier organización.
 */
@Component("tenantGuard")
@RequiredArgsConstructor
public class TenantGuard {

    private final MiembroOrganizacionRepository miembroOrganizacionRepository;
    private final UsuarioAppRepository usuarioAppRepository;

    public boolean esMiembro(UUID organizacionId, Authentication authentication) {
        if (esSuperAdmin(authentication)) {
            return true;
        }
        UUID usuarioId = resolverUsuarioId(authentication);
        if (usuarioId == null || organizacionId == null) {
            return false;
        }
        return miembroOrganizacionRepository
                .existsById(new com.garf.garfpay.modules.tenant.entity.MiembroOrganizacionId(organizacionId, usuarioId));
    }

    public boolean tieneRolEnOrganizacion(UUID organizacionId, Authentication authentication, String... codigosRolPermitidos) {
        if (esSuperAdmin(authentication)) {
            return true;
        }
        UUID usuarioId = resolverUsuarioId(authentication);
        if (usuarioId == null) {
            return false;
        }
        return miembroOrganizacionRepository.findByUsuarioId(usuarioId).stream()
                .filter(m -> m.getOrganizacion().getOrganizacionId().equals(organizacionId))
                .map(MiembroOrganizacion::getRol)
                .anyMatch(rol -> java.util.Arrays.asList(codigosRolPermitidos).contains(rol.getCodigo()));
    }

    private boolean esSuperAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_" + AppConstants.ROLE_SUPER_ADMIN));
    }

    private UUID resolverUsuarioId(Authentication authentication) {
        return usuarioAppRepository.findByNombreUsuario(authentication.getName())
                .map(u -> u.getUsuarioId())
                .orElse(null);
    }
}