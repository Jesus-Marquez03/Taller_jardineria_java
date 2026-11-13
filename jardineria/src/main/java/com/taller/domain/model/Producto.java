package com.taller.domain.model;

import java.math.BigDecimal;

/**
 * Representa un producto vendido por la jardinería y sus existencias.
 *
 * <p>Pertenece a la capa de dominio. Se relaciona con una
 * {@link GamaProducto} mediante el atributo {@code gama}. El stock se guarda
 * como entero y los precios como {@link BigDecimal}, el tipo apropiado para
 * dinero. La clase solo protege y valida sus datos; no ejecuta SQL.</p>
 */
public class Producto {
    private String codigoProducto;
    private String nombre;
    private String gama;
    private String dimensiones;
    private String proveedor;
    private String descripcion;
    private int cantidadEnStock;
    private BigDecimal precioVenta;
    private BigDecimal precioProveedor;

    /**
     * Construye un producto. Utiliza setters para impedir texto obligatorio
     * vacío, stock negativo y precios negativos.
     */
    public Producto(String codigoProducto, String nombre, String gama, String dimensiones, String proveedor,
                    String descripcion, int cantidadEnStock, BigDecimal precioVenta, BigDecimal precioProveedor) {
        setCodigoProducto(codigoProducto);
        setNombre(nombre);
        setGama(gama);
        this.dimensiones = dimensiones;
        this.proveedor = proveedor;
        this.descripcion = descripcion;
        setCantidadEnStock(cantidadEnStock);
        setPrecioVenta(precioVenta);
        setPrecioProveedor(precioProveedor);
    }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = Validaciones.textoRequerido(codigoProducto, "El codigo del producto"); }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Validaciones.textoRequerido(nombre, "El nombre"); }
    public String getGama() { return gama; }
    public void setGama(String gama) { this.gama = Validaciones.textoRequerido(gama, "La gama"); }
    public String getDimensiones() { return dimensiones; }
    public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getCantidadEnStock() { return cantidadEnStock; }
    public void setCantidadEnStock(int cantidadEnStock) { this.cantidadEnStock = Validaciones.enteroNoNegativo(cantidadEnStock, "El stock"); }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = Validaciones.decimalNoNegativo(precioVenta, "El precio de venta", false); }
    public BigDecimal getPrecioProveedor() { return precioProveedor; }
    public void setPrecioProveedor(BigDecimal precioProveedor) { this.precioProveedor = Validaciones.decimalNoNegativo(precioProveedor, "El precio del proveedor", true); }

    @Override public String toString() { return codigoProducto + " - " + nombre; }
}
