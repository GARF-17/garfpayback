CREATE TABLE notificaciones.punto_enlace_webhook (
    webhook_endpoint_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID NOT NULL REFERENCES tenant.organizacion(organizacion_id) ON DELETE RESTRICT,
    url_enlace TEXT NOT NULL,
    clave_secreta TEXT NOT NULL,
    esta_activo BOOLEAN DEFAULT TRUE,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notificaciones.envio_webhook (
    webhook_delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    webhook_endpoint_id UUID NOT NULL REFERENCES notificaciones.punto_enlace_webhook(webhook_endpoint_id) ON DELETE RESTRICT,
    nombre_evento VARCHAR(100) NOT NULL,
    payload JSONB,
    firma TEXT,
    codigo_respuesta INT,
    cuerpo_respuesta TEXT,
    exitoso BOOLEAN,
    conteo_reintentos INT DEFAULT 0,
    proximo_reintento_el TIMESTAMPTZ,
    enviado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notificaciones.dispositivo_usuario (
    dispositivo_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES identidad.usuario_app(usuario_id) ON DELETE CASCADE,
    token_push TEXT NOT NULL,
    plataforma VARCHAR(20) NOT NULL,
    esta_activo BOOLEAN DEFAULT TRUE,
    ultimo_uso_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, token_push)
);