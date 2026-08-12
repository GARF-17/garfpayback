package com.garf.garfpay.modules.auditoria.entity;

import com.garf.garfpay.modules.identidad.entity.UsuarioApp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "registro_auditoria", schema = "auditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "auditoria_id", updatable = false, nullable = false)
    private UUID auditoriaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioApp usuario;

    @Column(name = "nombre_modulo", length = 100)
    private String nombreModulo;

    @Column(name = "nombre_accion", length = 100)
    private String nombreAccion;

    @Column(name = "nombre_entidad", length = 100)
    private String nombreEntidad;

    @Column(name = "id_entidad")
    private UUID idEntidad;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valores_anteriores", columnDefinition = "jsonb")
    private Map<String, Object> valoresAnteriores;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valores_nuevos", columnDefinition = "jsonb")
    private Map<String, Object> valoresNuevos;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "agente_usuario", columnDefinition = "TEXT")
    private String agenteUsuario;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;
}