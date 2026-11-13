package com.taller.domain.model;

import java.time.LocalDate;

/**
 * Representa un pedido realizado por un cliente.
 *
 * <p>Usa {@link LocalDate} porque las columnas guardan fechas sin hora. La
 * fecha de entrega puede ser {@code null} mientras el pedido no se entregue.
 * Esta entidad valida presencia y formato básico; {@code PedidoService}
 * compara el orden lógico de las fechas y comprueba que el cliente exista.</p>
 */
public class Pedido {
    private Integer codigoPedido;
    private LocalDate fechaPedido;
    private LocalDate fechaEsperada;
    private LocalDate fechaEntrega;
    private String estado;
    private String comentarios;
    private Integer codigoCliente;

    /** Crea el pedido y valida los campos básicos mediante sus setters. */
    public Pedido(Integer codigoPedido, LocalDate fechaPedido, LocalDate fechaEsperada, LocalDate fechaEntrega,
                  String estado, String comentarios, Integer codigoCliente) {
        setCodigoPedido(codigoPedido);
        setFechaPedido(fechaPedido);
        setFechaEsperada(fechaEsperada);
        this.fechaEntrega = fechaEntrega;
        setEstado(estado);
        this.comentarios = comentarios;
        setCodigoCliente(codigoCliente);
    }

    public Integer getCodigoPedido() { return codigoPedido; }
    public void setCodigoPedido(Integer codigoPedido) { this.codigoPedido = Validaciones.enteroPositivo(codigoPedido == null ? 0 : codigoPedido, "El codigo del pedido"); }
    public LocalDate getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDate fechaPedido) { if (fechaPedido == null) throw new IllegalArgumentException("La fecha del pedido es obligatoria."); this.fechaPedido = fechaPedido; }
    public LocalDate getFechaEsperada() { return fechaEsperada; }
    public void setFechaEsperada(LocalDate fechaEsperada) { if (fechaEsperada == null) throw new IllegalArgumentException("La fecha esperada es obligatoria."); this.fechaEsperada = fechaEsperada; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = Validaciones.textoRequerido(estado, "El estado"); }
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    public Integer getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(Integer codigoCliente) { this.codigoCliente = Validaciones.enteroPositivo(codigoCliente == null ? 0 : codigoCliente, "El codigo del cliente"); }

    @Override public String toString() { return codigoPedido + " - " + estado; }
}
