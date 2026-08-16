package com.garf.garfpay.modules.pagos.entity;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;
import com.garf.garfpay.modules.tenant.entity.Organizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Entity
@Table(name = "metodo_pago_guardado", schema = "pagos")
public class MetodoPagoGuardado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "metodo_pago_id", updatable = false, nullable = false)
    private UUID metodoPagoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private NombreProveedor proveedor;

    // Token OPACO devuelto por el PSP tras tokenizar la tarjeta en el cliente.
    // GarfPay nunca almacena PAN, CVV ni fecha de expiración.
    @Column(name = "token_proveedor", nullable = false, columnDefinition = "TEXT")
    private String tokenProveedor;

    @Column(name = "marca_tarjeta", length = 20)
    private String marcaTarjeta;

    @Column(name = "ultimos_cuatro_digitos", length = 4)
    private String ultimosCuatroDigitos;

    @Builder.Default
    @Column(name = "es_predeterminado")
    private Boolean esPredeterminado = true;

    @Builder.Default
    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @CreationTimestamp
    @Column(name = "creado_el", updatable = false)
    private OffsetDateTime creadoEl;

    @Column(name = "eliminado_el")
    private OffsetDateTime eliminadoEl;
}