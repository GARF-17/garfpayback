CREATE TABLE pagos.solicitud_cobro (
    solicitud_cobro_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID NOT NULL REFERENCES tenant.organizacion(organizacion_id) ON DELETE RESTRICT,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo pagos.tipo_solicitud_pago NOT NULL,
    monto NUMERIC(18,2) NOT NULL CHECK (monto > 0),
    moneda VARCHAR(10) DEFAULT 'PEN',
    permite_pago_parcial BOOLEAN DEFAULT FALSE,
    expira_el TIMESTAMPTZ,
    esta_activo BOOLEAN DEFAULT TRUE,
    creado_por UUID REFERENCES identidad.usuario_app(usuario_id),
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    actualizado_el TIMESTAMPTZ,
    eliminado_el TIMESTAMPTZ
);

CREATE TABLE pagos.destino_solicitud_cobro (
    solicitud_cobro_id UUID NOT NULL REFERENCES pagos.solicitud_cobro(solicitud_cobro_id) ON DELETE RESTRICT,
    usuario_id UUID NOT NULL REFERENCES identidad.usuario_app(usuario_id) ON DELETE RESTRICT,
    estado pagos.estado_destino_cobro DEFAULT 'PENDIENTE',
    monto_personalizado NUMERIC(18,2),
    pagado_el TIMESTAMPTZ,
    PRIMARY KEY (solicitud_cobro_id, usuario_id)
);

CREATE TABLE pagos.transaccion_pago (
    transaccion_pago_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    solicitud_cobro_id UUID REFERENCES pagos.solicitud_cobro(solicitud_cobro_id) ON DELETE RESTRICT,
    usuario_pagador_id UUID REFERENCES identidad.usuario_app(usuario_id) ON DELETE RESTRICT,
    cuenta_liquidacion_id UUID REFERENCES tenant.cuenta_liquidacion(cuenta_liquidacion_id) ON DELETE RESTRICT,
    proveedor pagos.nombre_proveedor NOT NULL,
    id_transaccion_proveedor VARCHAR(150),
    referencia_proveedor VARCHAR(150),
    referencia_transaccion VARCHAR(150),
    id_traza VARCHAR(150),
    id_correlacion VARCHAR(150),
    clave_idempotencia VARCHAR(150) UNIQUE NOT NULL,
    monto NUMERIC(18,2) NOT NULL CHECK (monto > 0),
    comision_pasarela NUMERIC(18,4) DEFAULT 0,
    comision_plataforma NUMERIC(18,4) DEFAULT 0,
    monto_neto NUMERIC(18,2) GENERATED ALWAYS AS (
        CAST(monto - comision_pasarela - comision_plataforma AS NUMERIC(18,2))
    ) STORED,
    moneda VARCHAR(10) DEFAULT 'PEN',
    estado pagos.estado_transaccion DEFAULT 'PENDIENTE',
    motivo_fallo TEXT,
    metadatos JSONB,
    respuesta_proveedor JSONB,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    actualizado_el TIMESTAMPTZ
);

CREATE TABLE pagos.evento_proveedor (
    evento_proveedor_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaccion_pago_id UUID NOT NULL REFERENCES pagos.transaccion_pago(transaccion_pago_id) ON DELETE RESTRICT,
    proveedor pagos.nombre_proveedor NOT NULL,
    id_externo_evento_proveedor VARCHAR(150),
    estado_proveedor VARCHAR(100),
    payload JSONB,
    recibido_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_evento_proveedor_externo
    ON pagos.evento_proveedor (proveedor, id_externo_evento_proveedor)
    WHERE id_externo_evento_proveedor IS NOT NULL;

CREATE TABLE pagos.reembolso (
    reembolso_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaccion_pago_id UUID NOT NULL REFERENCES pagos.transaccion_pago(transaccion_pago_id) ON DELETE RESTRICT,
    monto NUMERIC(18,2) NOT NULL CHECK (monto > 0),
    motivo TEXT,
    estado pagos.estado_reembolso DEFAULT 'SOLICITADO',
    aprobado_por UUID REFERENCES identidad.usuario_app(usuario_id) ON DELETE RESTRICT,
    id_reembolso_proveedor VARCHAR(150),
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    actualizado_el TIMESTAMPTZ
);