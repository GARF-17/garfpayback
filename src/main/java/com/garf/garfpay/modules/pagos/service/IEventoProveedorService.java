package com.garf.garfpay.modules.pagos.service;

import com.garf.garfpay.modules.pagos.enums.NombreProveedor;

import java.util.Map;

public interface IEventoProveedorService {
    void procesarEventoEntrante(NombreProveedor proveedor, String firma, Map<String, Object> payload);
}