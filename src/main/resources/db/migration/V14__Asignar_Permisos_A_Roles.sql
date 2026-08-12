-- 1. SUPER_ADMIN: Le damos TODOS los permisos del sistema
INSERT INTO control_acceso.rol_permiso (rol_id, permiso_id)
SELECT r.rol_id, p.permiso_id
FROM control_acceso.rol r, control_acceso.permiso p
WHERE r.codigo = 'SUPER_ADMIN';

-- 2. ORG_ADMIN (Admin Corporativo / Comunidad): Todo menos pagar sus propios cobros
INSERT INTO control_acceso.rol_permiso (rol_id, permiso_id)
SELECT r.rol_id, p.permiso_id
FROM control_acceso.rol r, control_acceso.permiso p
WHERE r.codigo = 'ORG_ADMIN'
AND p.codigo IN ('CREATE_PAYMENT_REQUEST', 'VIEW_PAYMENT_REQUEST', 'MANAGE_USERS', 'MANAGE_ORGANIZATION', 'VIEW_REPORTS');

-- 3. TREASURER (Tesorero): Dedicado a ver números, ingresos y cuadrar cuentas
INSERT INTO control_acceso.rol_permiso (rol_id, permiso_id)
SELECT r.rol_id, p.permiso_id
FROM control_acceso.rol r, control_acceso.permiso p
WHERE r.codigo = 'TREASURER'
AND p.codigo IN ('VIEW_PAYMENT_REQUEST', 'VIEW_REPORTS');

-- 4. COLLECTOR (Cobrador): Solo lanza cobros (links) y revisa si se pagaron
INSERT INTO control_acceso.rol_permiso (rol_id, permiso_id)
SELECT r.rol_id, p.permiso_id
FROM control_acceso.rol r, control_acceso.permiso p
WHERE r.codigo = 'COLLECTOR'
AND p.codigo IN ('CREATE_PAYMENT_REQUEST', 'VIEW_PAYMENT_REQUEST');

-- 5. USER (Cliente Final / Cuenta Personal): Solo puede ver lo que debe y pagarlo
INSERT INTO control_acceso.rol_permiso (rol_id, permiso_id)
SELECT r.rol_id, p.permiso_id
FROM control_acceso.rol r, control_acceso.permiso p
WHERE r.codigo = 'USER'
AND p.codigo IN ('VIEW_PAYMENT_REQUEST', 'PAY_PAYMENT_REQUEST');