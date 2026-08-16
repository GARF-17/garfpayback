ALTER TABLE facturacion.plan_suscripcion
    ADD COLUMN es_plan_por_defecto BOOLEAN NOT NULL DEFAULT FALSE;

CREATE UNIQUE INDEX uk_plan_suscripcion_default_unico
    ON facturacion.plan_suscripcion (es_plan_por_defecto)
    WHERE es_plan_por_defecto = TRUE;

UPDATE facturacion.plan_suscripcion
SET es_plan_por_defecto = TRUE
WHERE frecuencia = 'MENSUAL';