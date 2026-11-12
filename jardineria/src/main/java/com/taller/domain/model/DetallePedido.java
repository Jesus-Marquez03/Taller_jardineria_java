package com.taller.domain.model;

import java.math.BigDecimal;

public class DetallePedido {
    private Integer codigoPedido;
    private String codigoProducto;
    private int cantidad;
    private BigDecimal precioUnidad;
    private int numeroLinea;

    public DetallePedido(Integer codigoPedido, String codigoProducto, int cantidad,
                         BigDecimal precioUnidad, int numeroLinea) {
        setCodigoPedido(codigoPedido);
        setCodigoProducto(codigoProducto);
        setCantidad(cantidad);
        setPrecioUnidad(precioUnidad);
        setNumeroLinea(numeroLinea);
    }

    public DetallePedidoId getId() { return new DetallePedidoId(codigoPedido, codigoProducto); }
    public Integer getCodigoPedido() { return codigoPedido; }
    public void setCodigoPedido(Integer codigoPedido) { this.codigoPedido = Validaciones.enteroPositivo(codigoPedido == null ? 0 : codigoPedido, "El codigo del pedido"); }
    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = Validaciones.textoRequerido(codigoProducto, "El codigo del producto"); }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = Validaciones.enteroPositivo(cantidad, "La cantidad"); }
    public BigDecimal getPrecioUnidad() { return precioUnidad; }
    public void setPrecioUnidad(BigDecimal precioUnidad) { this.precioUnidad = Validaciones.decimalNoNegativo(precioUnidad, "El precio por unidad", false); }
    public int getNumeroLinea() { return numeroLinea; }
    public void setNumeroLinea(int numeroLinea) { this.numeroLinea = Validaciones.enteroPositivo(numeroLinea, "El numero de linea"); }
}
