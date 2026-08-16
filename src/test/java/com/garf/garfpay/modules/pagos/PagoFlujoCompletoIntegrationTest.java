package com.garf.garfpay.modules.pagos;

import com.garf.garfpay.IntegrationTestBase;
import com.garf.garfpay.modules.contabilidad.entity.Tarifario;
import com.garf.garfpay.modules.contabilidad.repository.TarifarioRepository;
import com.garf.garfpay.modules.identidad.entity.PerfilUsuario;
import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import com.garf.garfpay.modules.identidad.enums.EstadoUsuario;
import com.garf.garfpay.modules.identidad.enums.TipoDocumento;
import com.garf.garfpay.modules.identidad.repository.UsuarioAppRepository;
import com.garf.garfpay.modules.pagos.dto.request.CrearSolicitudCobroRequestDTO;
import com.garf.garfpay.modules.pagos.dto.request.ProcesarPagoRequestDTO;
import com.garf.garfpay.modules.pagos.dto.response.SolicitudCobroResponseDTO;
import com.garf.garfpay.modules.pagos.dto.response.TransaccionResponseDTO;
import com.garf.garfpay.modules.pagos.enums.EstadoTransaccion;
import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.pagos.enums.TipoSolicitudPago;
import com.garf.garfpay.modules.pagos.repository.TransaccionPagoRepository;
import com.garf.garfpay.modules.pagos.service.ISolicitudCobroService;
import com.garf.garfpay.modules.pagos.service.ITransaccionPagoService;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import com.garf.garfpay.modules.tenant.enums.CategoriaOrganizacion;
import com.garf.garfpay.modules.tenant.enums.EstadoOrganizacion;
import com.garf.garfpay.modules.tenant.enums.TipoOrganizacion;
import com.garf.garfpay.modules.tenant.repository.OrganizacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PagoFlujoCompletoIntegrationTest extends IntegrationTestBase {

    @Autowired private OrganizacionRepository organizacionRepository;
    @Autowired private UsuarioAppRepository usuarioAppRepository;
    @Autowired private TarifarioRepository tarifarioRepository;
    @Autowired private ISolicitudCobroService solicitudCobroService;
    @Autowired private ITransaccionPagoService transaccionPagoService;
    @Autowired private TransaccionPagoRepository transaccionPagoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void debeProcesarPagoCompletoYCalcularComisionDesdeTarifario() {
        // Organización activa
        Organizacion org = organizacionRepository.save(Organizacion.builder()
                .razonSocial("Colegio de Prueba")
                .tipoOrganizacion(TipoOrganizacion.COMUNIDAD)
                .categoria(CategoriaOrganizacion.COLEGIO)
                .estado(EstadoOrganizacion.ACTIVA)
                .build());

        // Tarifario específico
        tarifarioRepository.save(Tarifario.builder()
                .organizacion(org)
                .proveedor(NombreProveedor.CULQI)
                .comisionPorcentaje(new BigDecimal("0.025"))
                .comisionFija(new BigDecimal("0.50"))
                .vigenteDesde(OffsetDateTime.now().minusDays(1))
                .build());

        // Usuario pagador
        UsuarioApp pagador = crearUsuarioDePrueba("pagador.test");

        // Solicitud de cobro + destino
        SolicitudCobroResponseDTO solicitud = solicitudCobroService.crearSolicitudCobro(
                org.getOrganizacionId(),
                new CrearSolicitudCobroRequestDTO(
                        "Pensión Marzo", "Cuota mensual", TipoSolicitudPago.PENSION,
                        new BigDecimal("100.00"), "PEN", false, null, List.of(pagador.getUsuarioId())),
                "pagador.test");

        // Procesar pago (usa FakePasarelaGateway del perfil test)
        TransaccionResponseDTO transaccion = transaccionPagoService.procesarPago(
                pagador.getUsuarioId(),
                new ProcesarPagoRequestDTO(
                        solicitud.solicitudCobroId(), NombreProveedor.CULQI,
                        "test-clave-" + UUID.randomUUID(), new BigDecimal("100.00"), null));

        // Verificaciones — la comisión debe venir del Tarifario, NO hardcodeada
        assertThat(transaccion.estado()).isEqualTo(EstadoTransaccion.COMPLETADO.name());

        var persistida = transaccionPagoRepository.findById(transaccion.transaccionPagoId()).orElseThrow();
        BigDecimal comisionEsperada = new BigDecimal("100.00")
                .multiply(new BigDecimal("0.025")).add(new BigDecimal("0.50")); // 3.00
        assertThat(persistida.getComisionPasarela()).isEqualByComparingTo(comisionEsperada);
        assertThat(persistida.getIdTransaccionProveedor()).startsWith("FAKE-");
        assertThat(persistida.getIdTransaccionProveedor()).doesNotContain("SIMULACION");
    }

    @Test
    void debeRechazarPagoSinTarifarioConfigurado() {
        Organizacion org = organizacionRepository.save(Organizacion.builder()
                .razonSocial("Org Sin Tarifario")
                .tipoOrganizacion(TipoOrganizacion.PERSONAL)
                .categoria(CategoriaOrganizacion.OTRO)
                .estado(EstadoOrganizacion.ACTIVA)
                .build());

        UsuarioApp pagador = crearUsuarioDePrueba("sintarifario.test");

        SolicitudCobroResponseDTO solicitud = solicitudCobroService.crearSolicitudCobro(
                org.getOrganizacionId(),
                new CrearSolicitudCobroRequestDTO("Cobro X", null, TipoSolicitudPago.OTRO,
                        BigDecimal.TEN, "PEN", false, null, List.of(pagador.getUsuarioId())),
                "sintarifario.test");

        org.junit.jupiter.api.Assertions.assertThrows(
                com.garf.garfpay.shared.exception.BusinessRuleException.class,
                () -> transaccionPagoService.procesarPago(pagador.getUsuarioId(),
                        new ProcesarPagoRequestDTO(solicitud.solicitudCobroId(), NombreProveedor.CULQI,
                                "clave-sin-tarifario", BigDecimal.TEN, null)));
    }

    private UsuarioApp crearUsuarioDePrueba(String nombreUsuario) {
        PerfilUsuario perfil = PerfilUsuario.builder()
                .nombres("Test").apellidos("User")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento(nombreUsuario + "-doc")
                .correo(nombreUsuario + "@test.com")
                .build();

        UsuarioApp usuario = UsuarioApp.builder()
                .perfil(perfil)
                .nombreUsuario(nombreUsuario)
                .claveHash(passwordEncoder.encode("Password123!"))
                .estado(EstadoUsuario.ACTIVO)
                .intentosFallidosLogin(0)
                .build();

        return usuarioAppRepository.save(usuario);
    }
}