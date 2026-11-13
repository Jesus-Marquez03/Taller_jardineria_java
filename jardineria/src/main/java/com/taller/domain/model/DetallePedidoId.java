package com.taller.domain.model;

/**
 * Identificador compuesto e inmutable de un detalle de pedido.
 *
 * <p>Un {@code record} crea automáticamente constructor, accesores,
 * {@code equals} y {@code hashCode}. Esto permite usar juntas las dos columnas
 * de la clave primaria sin inventar un único código artificial.</p>
 *
 * @param codigoPedido primera parte de la clave
 * @param codigoProducto segunda parte de la clave
 */
public record DetallePedidoId(Integer codigoPedido, String codigoProducto) {
    /** Constructor compacto que valida las dos partes antes de crear el record. */
    public DetallePedidoId {
        codigoPedido = Validaciones.enteroPositivo(codigoPedido == null ? 0 : codigoPedido, "El codigo del pedido");
        codigoProducto = Validaciones.textoRequerido(codigoProducto, "El codigo del producto");
    }
}
