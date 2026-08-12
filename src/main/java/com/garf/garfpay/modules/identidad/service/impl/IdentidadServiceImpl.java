package com.garf.garfpay.modules.identidad.service.impl;

import com.garf.garfpay.modules.control_acceso.entity.Rol;
import com.garf.garfpay.modules.control_acceso.entity.UsuarioRol;
import com.garf.garfpay.modules.control_acceso.entity.UsuarioRolId;
import com.garf.garfpay.modules.control_acceso.repository.RolRepository;
import com.garf.garfpay.modules.identidad.dto.request.LoginRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.RegistroUsuarioRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.SolicitarCodigoRequestDTO;
import com.garf.garfpay.modules.identidad.dto.request.ValidarCodigoRequestDTO;
import com.garf.garfpay.modules.identidad.dto.response.LoginResponseDTO;
import com.garf.garfpay.modules.identidad.dto.response.RegistroUsuarioResponseDTO;
import com.garf.garfpay.modules.identidad.entity.CodigoVerificacion;
import com.garf.garfpay.modules.identidad.entity.PerfilUsuario;
import com.garf.garfpay.modules.identidad.entity.SesionUsuario;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.enums.EstadoUsuario;
import com.garf.garfpay.modules.identidad.mapper.UsuarioMapper;
import com.garf.garfpay.modules.identidad.repository.CodigoVerificacionRepository;
import com.garf.garfpay.modules.identidad.repository.PerfilUsuarioRepository;
import com.garf.garfpay.modules.identidad.repository.SesionUsuarioRepository;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.identidad.service.IIdentidadService;
import com.garf.garfpay.shared.exception.BusinessRuleException;
import com.garf.garfpay.shared.exception.ConflictException;
import com.garf.garfpay.shared.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentidadServiceImpl implements IIdentidadService {

    private final UsuarioAppRepository usuarioAppRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final SesionUsuarioRepository sesionUsuarioRepository;
    private final CodigoVerificacionRepository codigoVerificacionRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletRequest httpRequest;

    @Value("${security.login.max-attempts:3}")
    private int maxIntentosFallidos;

    @Value("${security.verification-code.expiration-minutes:15}")
    private int expiracionCodigoMinutos;

    @Value("${security.verification-code.max-attempts:3}")
    private int maxIntentosCodigo;

    @Override
    @Transactional
    public RegistroUsuarioResponseDTO registrarUsuario(RegistroUsuarioRequestDTO request) {

        if (usuarioAppRepository.findByNombreUsuario(request.nombreUsuario()).isPresent()) {
            throw new ConflictException("El nombre de usuario ya está en uso.");
        }
        if (perfilUsuarioRepository.existsByCorreo(request.correo())) {
            throw new ConflictException("El correo electrónico ya está registrado.");
        }
        if (perfilUsuarioRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new ConflictException("El número de documento ya está registrado.");
        }

        PerfilUsuario perfil = usuarioMapper.toPerfilUsuario(request);
        UsuarioApp usuario = usuarioMapper.toUsuarioApp(request);

        usuario.setPerfil(perfil);
        usuario.setClaveHash(passwordEncoder.encode(request.clave()));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setIntentosFallidosLogin(0);
        usuario.setMfaHabilitado(false);
        Rol rolUser = rolRepository.findByCodigo("USER")
                .orElseThrow(() -> new BusinessRuleException("Error crítico: Rol base USER no encontrado en el sistema."));

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setId(new UsuarioRolId());
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rolUser);

        usuario.getRoles().add(usuarioRol);
        UsuarioApp usuarioGuardado = usuarioAppRepository.save(usuario);

        return usuarioMapper.toRegistroResponse(usuarioGuardado);
    }

    @Override
    @Transactional(noRollbackFor = BusinessRuleException.class)
    public LoginResponseDTO login(LoginRequestDTO request) {

        UsuarioApp usuario = usuarioAppRepository.findByNombreUsuarioWithPerfil(request.nombreUsuario())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas."));

        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            throw new BusinessRuleException("Tu cuenta ha sido bloqueada por múltiples intentos fallidos. Contacta a soporte.");
        }
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new BusinessRuleException("Tu cuenta no está activa. Estado actual: " + usuario.getEstado());
        }

        if (!passwordEncoder.matches(request.clave(), usuario.getClaveHash())) {
            manejarIntentoFallido(usuario);
        }

        usuario.setIntentosFallidosLogin(0);
        usuario.setUltimoLoginEl(LocalDateTime.now());
        usuarioAppRepository.save(usuario);

        // Extraer los roles reales de la BD y pasarlos al JWT
        String[] rolesArray = usuario.getRoles().stream()
                .map(ur -> ur.getRol().getCodigo())
                .toArray(String[]::new);

        if (rolesArray.length == 0) {
            throw new BusinessRuleException("Tu cuenta no tiene ningún rol asignado. Contacta a soporte.");
        }

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(usuario.getNombreUsuario())
                .password(usuario.getClaveHash())
                .roles(rolesArray)
                .build();

        String tokenAcceso = jwtService.generateToken(userDetails);
        String tokenRefresco = UUID.randomUUID().toString();

        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = httpRequest.getRemoteAddr();
        }

        String userAgent = httpRequest.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            userAgent = "Dispositivo Desconocido";
        }

        SesionUsuario sesion = SesionUsuario.builder()
                .usuario(usuario)
                .hashTokenRefresco(tokenRefresco)
                .direccionIp(ipAddress)
                .agenteUsuario(userAgent)
                .estaActiva(true)
                .loginEl(LocalDateTime.now())
                .build();
        sesionUsuarioRepository.save(sesion);

        return new LoginResponseDTO(
                tokenAcceso,
                tokenRefresco,
                usuario.getUsuarioId(),
                usuario.getPerfil().getNombres(),
                usuario.getPerfil().getApellidos(),
                usuario.getPerfil().getCorreo(),
                rolesArray[0]
        );
    }

    private void manejarIntentoFallido(UsuarioApp usuario) {
        int intentos = usuario.getIntentosFallidosLogin() + 1;
        usuario.setIntentosFallidosLogin(intentos);

        if (intentos >= maxIntentosFallidos) {
            usuario.setEstado(EstadoUsuario.BLOQUEADO);
            usuarioAppRepository.save(usuario);
            throw new BusinessRuleException("Has superado el límite de intentos fallidos. Tu cuenta ha sido bloqueada permanentemente.");
        }

        usuarioAppRepository.save(usuario);
        int intentosRestantes = maxIntentosFallidos - intentos;
        throw new BusinessRuleException("Credenciales inválidas. Te quedan " + intentosRestantes + " intentos antes de bloquear la cuenta.");
    }


    @Transactional
    public void generarYEnviarCodigoVerificacion(SolicitarCodigoRequestDTO request) {
        UsuarioApp usuario = usuarioAppRepository.findById(request.usuarioId())
                .orElseThrow(() -> new BusinessRuleException("Usuario no encontrado"));

        String codigoSecreto = String.format("%06d", new java.security.SecureRandom().nextInt(1000000));

        CodigoVerificacion codigoEntity = CodigoVerificacion.builder()
                .usuario(usuario)
                .tipo(request.tipo())
                .codigoHash(passwordEncoder.encode(codigoSecreto))
                .intentos(0)
                .expiraEl(LocalDateTime.now().plusMinutes(expiracionCodigoMinutos))
                .build();

        codigoVerificacionRepository.save(codigoEntity);

        System.out.println("=================================================");
        System.out.println("📬 CÓDIGO OTP PARA " + usuario.getPerfil().getCorreo() + ": " + codigoSecreto);
        System.out.println("=================================================");
    }

    @Override
    @Transactional(noRollbackFor = BusinessRuleException.class)
    public boolean validarCodigoVerificacion(ValidarCodigoRequestDTO request) {

        CodigoVerificacion codigoEntity = codigoVerificacionRepository
                .findTopByUsuarioUsuarioIdAndTipoAndUsadoElIsNullAndExpiraElAfterOrderByCreadoElDesc(
                        request.usuarioId(), request.tipo(), LocalDateTime.now())
                .orElseThrow(() -> new BusinessRuleException("El código no existe o ha expirado. Solicita uno nuevo."));

        if (codigoEntity.getIntentos() >= maxIntentosCodigo) {
            throw new BusinessRuleException("Has superado el límite de intentos para este código. Solicita uno nuevo.");
        }

        if (!passwordEncoder.matches(request.codigo(), codigoEntity.getCodigoHash())) {
            codigoEntity.setIntentos(codigoEntity.getIntentos() + 1);
            codigoVerificacionRepository.save(codigoEntity);
            int restantes = maxIntentosCodigo - codigoEntity.getIntentos();
            throw new BusinessRuleException("Código incorrecto. Te quedan " + restantes + " intentos.");
        }

        codigoEntity.setUsadoEl(LocalDateTime.now());
        codigoVerificacionRepository.save(codigoEntity);

        UsuarioApp usuario = codigoEntity.getUsuario();
        switch (request.tipo()) {
            case CORREO -> usuario.getPerfil().setCorreoVerificado(true);
            case TELEFONO -> usuario.getPerfil().setTelefonoVerificado(true);
        }
        usuarioAppRepository.save(usuario);

        return true;
    }
}