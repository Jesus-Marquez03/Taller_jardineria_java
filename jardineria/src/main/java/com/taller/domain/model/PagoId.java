package com.taller.domain.model;

/**
 * Identificador compuesto e inmutable de un pago.
 *
 * <p>El {@code record} representa exactamente la clave primaria formada por el
 * cliente y el identificador de transacción. Java genera sus accesores y la
 * comparación por valor.</p>
 *
 * @param codigoCliente cliente propietario del pago
 * @param idTransaccion código de la transacción dentro del cliente
 */
public record PagoId(Integer codigoCliente, String idTransaccion) {
    /** Constructor compacto que impide crear una clave incompleta. */
    public PagoId {
        codigoCliente = Validaciones.enteroPositivo(codigoCliente == null ? 0 : codigoCliente, "El codigo del cliente");
        idTransaccion = Validaciones.textoRequerido(idTransaccion, "El identificador de transaccion");
    }
}
