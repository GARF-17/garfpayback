CREATE TABLE contabilidad.evento_transaccion (
    evento_transaccion_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaccion_pago_id UUID NOT NULL REFERENCES pagos.transaccion_pago(transaccion_pago_id) ON DELETE CASCADE,
    codigo_evento VARCHAR(100) NOT NULL,
    descripcion TEXT,
    payload JSONB,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contabilidad.tarifario (
    tarifario_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID REFERENCES tenant.organizacion(organizacion_id) ON DELETE CASCADE,
    proveedor pagos.nombre_proveedor NOT NULL,
    comision_porcentaje NUMERIC(5,4) NOT NULL DEFAULT 0,
    comision_fija NUMERIC(18,2) NOT NULL DEFAULT 0,
    vigente_desde TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vigente_hasta TIMESTAMPTZ,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_tarifario_vigencia CHECK (vigente_hasta IS NULL OR vigente_hasta > vigente_desde)
);

CREATE UNIQUE INDEX uk_tarifario_default_vigente
    ON contabilidad.tarifario (proveedor)
    WHERE organizacion_id IS NULL AND vigente_hasta IS NULL;

CREATE TABLE contabilidad.liquidacion (
    liquidacion_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID NOT NULL REFERENCES tenant.organizacion(organizacion_id) ON DELETE RESTRICT,
    cuenta_liquidacion_id UUID NOT NULL REFERENCES tenant.cuenta_liquidacion(cuenta_liquidacion_id) ON DELETE RESTRICT,
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    moneda VARCHAR(10) DEFAULT 'PEN',
    monto_bruto NUMERIC(18,2) NOT NULL CHECK (monto_bruto >= 0),
    monto_comisiones NUMERIC(18,2) NOT NULL CHECK (monto_comisiones >= 0),
    monto_neto NUMERIC(18,2) GENERATED ALWAYS AS (
        CAST(monto_bruto - monto_comisiones AS NUMERIC(18,2))
    ) STORED,
    estado contabilidad.estado_liquidacion DEFAULT 'PENDIENTE',
    referencia_transferencia VARCHAR(150),
    liquidado_el TIMESTAMPTZ,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_liquidacion_periodo CHECK (periodo_fin >= periodo_inicio)
);

CREATE TABLE contabilidad.liquidacion_detalle (
    liquidacion_id UUID NOT NULL REFERENCES contabilidad.liquidacion(liquidacion_id) ON DELETE CASCADE,
    transaccion_pago_id UUID NOT NULL REFERENCES pagos.transaccion_pago(transaccion_pago_id) ON DELETE RESTRICT,
    PRIMARY KEY (liquidacion_id, transaccion_pago_id)
);

CREATE UNIQUE INDEX uk_liquidacion_detalle_transaccion_unica
    ON contabilidad.liquidacion_detalle (transaccion_pago_id);