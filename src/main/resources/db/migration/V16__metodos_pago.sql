CREATE TABLE pagos.metodo_pago_guardado (
    metodo_pago_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID NOT NULL REFERENCES tenant.organizacion(organizacion_id) ON DELETE RESTRICT,
    proveedor pagos.nombre_proveedor NOT NULL,
    token_proveedor TEXT NOT NULL,
    marca_tarjeta VARCHAR(20),
    ultimos_cuatro_digitos VARCHAR(4),
    es_predeterminado BOOLEAN DEFAULT TRUE,
    esta_activo BOOLEAN DEFAULT TRUE,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    eliminado_el TIMESTAMPTZ
);

CREATE UNIQUE INDEX uk_metodo_pago_predeterminado_activo
    ON pagos.metodo_pago_guardado (organizacion_id)
    WHERE es_predeterminado = TRUE AND esta_activo = TRUE;

CREATE INDEX idx_metodo_pago_organizacion ON pagos.metodo_pago_guardado(organizacion_id);