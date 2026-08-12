CREATE TABLE identidad.perfil_usuario (
    perfil_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    tipo_documento identidad.tipo_documento NOT NULL,
    numero_documento VARCHAR(30) UNIQUE NOT NULL,
    correo VARCHAR(120) UNIQUE NOT NULL,
    telefono VARCHAR(20) UNIQUE,
    correo_verificado BOOLEAN DEFAULT FALSE,
    telefono_verificado BOOLEAN DEFAULT FALSE,
    fecha_nacimiento DATE,
    url_avatar TEXT,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    actualizado_el TIMESTAMPTZ,
    eliminado_el TIMESTAMPTZ
);

CREATE TABLE identidad.usuario_app (
    usuario_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    perfil_id UUID NOT NULL REFERENCES identidad.perfil_usuario(perfil_id),
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    clave_hash VARCHAR(255) NOT NULL,
    estado identidad.estado_usuario DEFAULT 'ACTIVO',
    intentos_fallidos_login INT DEFAULT 0,
    ultimo_login_el TIMESTAMPTZ,
    mfa_habilitado BOOLEAN DEFAULT FALSE,
    mfa_secreto VARCHAR(255),
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    actualizado_el TIMESTAMPTZ,
    eliminado_el TIMESTAMPTZ
);

CREATE TABLE identidad.sesion_usuario (
    sesion_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES identidad.usuario_app(usuario_id) ON DELETE CASCADE,
    hash_token_refresco TEXT,
    direccion_ip VARCHAR(45),
    nombre_dispositivo VARCHAR(150),
    agente_usuario TEXT,
    pais VARCHAR(100),
    ciudad VARCHAR(100),
    esta_activa BOOLEAN DEFAULT TRUE,
    login_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    logout_el TIMESTAMPTZ
);

CREATE TABLE identidad.codigo_verificacion (
    codigo_verificacion_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES identidad.usuario_app(usuario_id) ON DELETE CASCADE,
    tipo identidad.tipo_verificacion NOT NULL,
    codigo_hash VARCHAR(255) NOT NULL,
    intentos INT DEFAULT 0,
    expira_el TIMESTAMPTZ NOT NULL,
    usado_el TIMESTAMPTZ,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);