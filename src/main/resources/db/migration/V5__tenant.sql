CREATE TABLE tenant.organizacion (
    organizacion_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    razon_social VARCHAR(150) NOT NULL,
    tipo_organizacion tenant.tipo_organizacion NOT NULL,
    categoria tenant.categoria_organizacion NOT NULL,
    documento_identidad VARCHAR(20),
    correo VARCHAR(120),
    telefono VARCHAR(20),
    direccion TEXT,
    url_logo TEXT,
    estado tenant.estado_organizacion DEFAULT 'ACTIVA',
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    actualizado_el TIMESTAMPTZ,
    eliminado_el TIMESTAMPTZ
);

CREATE TABLE tenant.miembro_organizacion (
    organizacion_id UUID NOT NULL REFERENCES tenant.organizacion(organizacion_id) ON DELETE RESTRICT,
    usuario_id UUID NOT NULL REFERENCES identidad.usuario_app(usuario_id) ON DELETE CASCADE,
    rol_id BIGINT NOT NULL REFERENCES control_acceso.rol(rol_id) ON DELETE RESTRICT,
    vinculado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (organizacion_id, usuario_id)
);

CREATE TABLE tenant.historial_rol_miembro (
    historial_rol_miembro_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    rol_anterior_id BIGINT REFERENCES control_acceso.rol(rol_id) ON DELETE RESTRICT,
    rol_nuevo_id BIGINT NOT NULL REFERENCES control_acceso.rol(rol_id) ON DELETE RESTRICT,
    cambiado_por UUID REFERENCES identidad.usuario_app(usuario_id) ON DELETE RESTRICT,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organizacion_id, usuario_id)
        REFERENCES tenant.miembro_organizacion(organizacion_id, usuario_id) ON DELETE CASCADE
);

CREATE TABLE tenant.cuenta_liquidacion (
    cuenta_liquidacion_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID NOT NULL REFERENCES tenant.organizacion(organizacion_id) ON DELETE RESTRICT,
    nombre_banco VARCHAR(100) NOT NULL,
    moneda VARCHAR(10) DEFAULT 'PEN',
    numero_cuenta VARCHAR(50),
    cci VARCHAR(50) NOT NULL,
    titular_cuenta VARCHAR(150),
    telefono_yape VARCHAR(20),
    es_principal BOOLEAN DEFAULT TRUE,
    esta_activa BOOLEAN DEFAULT TRUE,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    actualizado_el TIMESTAMPTZ,
    eliminado_el TIMESTAMPTZ
);

CREATE TABLE tenant.auditoria_cuenta_liquidacion (
    auditoria_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cuenta_liquidacion_id UUID NOT NULL REFERENCES tenant.cuenta_liquidacion(cuenta_liquidacion_id) ON DELETE RESTRICT,
    cambiado_por UUID NOT NULL REFERENCES identidad.usuario_app(usuario_id) ON DELETE RESTRICT,
    cci_anterior VARCHAR(50),
    cci_nuevo VARCHAR(50),
    direccion_ip VARCHAR(45),
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);