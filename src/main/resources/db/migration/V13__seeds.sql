INSERT INTO control_acceso.rol (codigo, nombre, descripcion, ambito, es_sistema)
VALUES
('SUPER_ADMIN', 'Super Administrador', 'Administrador global del núcleo de GARFPAY', 'PLATAFORMA', true),
('ORG_ADMIN', 'Administrador Organización', 'Administrador principal del negocio cliente (Colegio, Universidad, Empresa, Comunidad)', 'ORGANIZACION', true),
('TREASURER', 'Tesorero', 'Encargado financiero para control de conciliaciones y CCIs', 'ORGANIZACION', true),
('COLLECTOR', 'Cobrador / Responsable', 'Encargado de lanzar cobros y gestionar cuentas de destino', 'ORGANIZACION', true),
('USER', 'Usuario / Pagador', 'Cliente final que efectúa el pago', 'ORGANIZACION', true);

INSERT INTO control_acceso.permiso (codigo, descripcion)
VALUES
('CREATE_PAYMENT_REQUEST', 'Permite generar cobros individuales o masivos'),
('VIEW_PAYMENT_REQUEST', 'Permite ver los cobros pendientes, vencidos o completados'),
('PAY_PAYMENT_REQUEST', 'Permite activar las APIs adquirentes para procesar el pago'),
('MANAGE_USERS', 'Permite la administración de accesos y asignación de roles'),
('MANAGE_ORGANIZATION', 'Permite actualizar datos comerciales, cuentas CCI y webhooks'),
('VIEW_REPORTS', 'Permite visualizar analíticas de recaudación en tiempo real');