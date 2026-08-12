CREATE TABLE cumplimiento.verificacion_kyc (
    verificacion_kyc_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID REFERENCES tenant.organizacion(organizacion_id) ON DELETE RESTRICT,
    nombre_proveedor VARCHAR(100),
    payload_solicitud JSONB,
    payload_respuesta JSONB,
    estado cumplimiento.estado_kyc DEFAULT 'PENDIENTE',
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);