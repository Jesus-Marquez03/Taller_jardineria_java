package com.taller.domain.model;

public record PagoId(Integer codigoCliente, String idTransaccion) {
    public PagoId {
        codigoCliente = Validaciones.enteroPositivo(codigoCliente == null ? 0 : codigoCliente, "El codigo del cliente");
        idTransaccion = Validaciones.textoRequerido(idTransaccion, "El identificador de transaccion");
    }
}
