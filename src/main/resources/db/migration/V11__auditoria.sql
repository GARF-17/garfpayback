CREATE TABLE auditoria.registro_auditoria (
    auditoria_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID REFERENCES identidad.usuario_app(usuario_id) ON DELETE RESTRICT,
    nombre_modulo VARCHAR(100),
    nombre_accion VARCHAR(100),
    nombre_entidad VARCHAR(100),
    id_entidad UUID,
    valores_anteriores JSONB,
    valores_nuevos JSONB,
    direccion_ip VARCHAR(45),
    agente_usuario TEXT,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);