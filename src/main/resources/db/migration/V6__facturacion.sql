CREATE TABLE facturacion.plan_suscripcion (
    plan_suscripcion_id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(18,2) NOT NULL,
    frecuencia facturacion.frecuencia_suscripcion NOT NULL,
    esta_activo BOOLEAN DEFAULT TRUE,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE facturacion.suscripcion_organizacion (
    suscripcion_organizacion_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id UUID NOT NULL REFERENCES tenant.organizacion(organizacion_id) ON DELETE RESTRICT,
    plan_suscripcion_id BIGINT NOT NULL REFERENCES facturacion.plan_suscripcion(plan_suscripcion_id) ON DELETE RESTRICT,
    inicia_el DATE NOT NULL,
    termina_el DATE NOT NULL,
    esta_activa BOOLEAN DEFAULT TRUE,
    creado_el TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_suscripcion_organizacion_periodo CHECK (termina_el >= inicia_el)
);