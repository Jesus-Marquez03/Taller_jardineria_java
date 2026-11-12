package com.taller.domain.model;

public record DetallePedidoId(Integer codigoPedido, String codigoProducto) {
    public DetallePedidoId {
        codigoPedido = Validaciones.enteroPositivo(codigoPedido == null ? 0 : codigoPedido, "El codigo del pedido");
        codigoProducto = Validaciones.textoRequerido(codigoProducto, "El codigo del producto");
    }
}
