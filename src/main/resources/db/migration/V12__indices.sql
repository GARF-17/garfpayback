-- Pagos
CREATE INDEX idx_transaccion_pago_estado ON pagos.transaccion_pago(estado);
CREATE INDEX idx_transaccion_pago_pagador ON pagos.transaccion_pago(usuario_pagador_id);
CREATE INDEX idx_transaccion_pago_creado ON pagos.transaccion_pago(creado_el);
CREATE INDEX idx_transaccion_pago_solicitud_cobro ON pagos.transaccion_pago(solicitud_cobro_id);
CREATE INDEX idx_transaccion_pago_cuenta_liquidacion ON pagos.transaccion_pago(cuenta_liquidacion_id);
CREATE INDEX idx_solicitud_cobro_organizacion ON pagos.solicitud_cobro(organizacion_id);
CREATE INDEX idx_solicitud_cobro_activo_expira ON pagos.solicitud_cobro(esta_activo, expira_el);
CREATE INDEX idx_destino_solicitud_cobro_usuario ON pagos.destino_solicitud_cobro(usuario_id);
CREATE INDEX idx_evento_proveedor_transaccion ON pagos.evento_proveedor(transaccion_pago_id);
CREATE INDEX idx_evento_proveedor_payload ON pagos.evento_proveedor USING GIN (payload);
CREATE INDEX idx_reembolso_transaccion ON pagos.reembolso(transaccion_pago_id);
CREATE INDEX idx_reembolso_estado ON pagos.reembolso(estado);

-- Tenant
CREATE INDEX idx_organizacion_tipo ON tenant.organizacion(tipo_organizacion);
CREATE INDEX idx_miembro_organizacion_usuario ON tenant.miembro_organizacion(usuario_id);
CREATE INDEX idx_historial_rol_miembro_org_usuario ON tenant.historial_rol_miembro(organizacion_id, usuario_id);

-- Facturación
CREATE INDEX idx_suscripcion_organizacion_org ON facturacion.suscripcion_organizacion(organizacion_id);

-- Contabilidad
CREATE INDEX idx_liquidacion_organizacion ON contabilidad.liquidacion(organizacion_id);
CREATE INDEX idx_liquidacion_estado ON contabilidad.liquidacion(estado);
CREATE INDEX idx_tarifario_organizacion ON contabilidad.tarifario(organizacion_id);

-- Notificaciones
CREATE INDEX idx_envio_webhook_enlace ON notificaciones.envio_webhook(webhook_endpoint_id);
CREATE INDEX idx_envio_webhook_payload ON notificaciones.envio_webhook USING GIN (payload);
CREATE INDEX idx_dispositivo_usuario_usuario ON notificaciones.dispositivo_usuario(usuario_id);

-- Identidad
CREATE INDEX idx_codigo_verificacion_usuario ON identidad.codigo_verificacion(usuario_id, tipo);
CREATE INDEX idx_usuario_app_username ON identidad.usuario_app(nombre_usuario);

-- Auditoría
CREATE INDEX idx_registro_auditoria_usuario ON auditoria.registro_auditoria(usuario_id);
CREATE INDEX idx_registro_auditoria_nuevos ON auditoria.registro_auditoria USING GIN (valores_nuevos);