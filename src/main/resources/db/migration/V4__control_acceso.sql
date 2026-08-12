CREATE TABLE control_acceso.rol (
    rol_id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    ambito control_acceso.ambito_rol NOT NULL DEFAULT 'ORGANIZACION',
    es_sistema BOOLEAN DEFAULT FALSE,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE control_acceso.permiso (
    permiso_id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE control_acceso.rol_permiso (
    rol_id BIGINT NOT NULL REFERENCES control_acceso.rol(rol_id) ON DELETE CASCADE,
    permiso_id BIGINT NOT NULL REFERENCES control_acceso.permiso(permiso_id) ON DELETE CASCADE,
    PRIMARY KEY (rol_id, permiso_id)
);

CREATE TABLE control_acceso.usuario_rol (
    usuario_id UUID NOT NULL REFERENCES identidad.usuario_app(usuario_id) ON DELETE CASCADE,
    rol_id BIGINT NOT NULL REFERENCES control_acceso.rol(rol_id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, rol_id)
);