package com.taller.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa un pago registrado para un cliente.
 *
 * <p>Su clave primaria es compuesta: código del cliente e identificador de
 * transacción. {@link LocalDate} representa el día del pago y
 * {@link BigDecimal} conserva el total monetario sin perder precisión.</p>
 */
public class Pago {
    private Integer codigoCliente;
    private String formaPago;
    private String idTransaccion;
    private LocalDate fechaPago;
    private BigDecimal total;

    /** Crea un pago y valida identificadores, fecha, forma de pago y total. */
    public Pago(Integer codigoCliente, String formaPago, String idTransaccion, LocalDate fechaPago, BigDecimal total) {
        setCodigoCliente(codigoCliente);
        setFormaPago(formaPago);
        setIdTransaccion(idTransaccion);
        setFechaPago(fechaPago);
        setTotal(total);
    }

    /**
     * Une las dos columnas que identifican de manera única el pago.
     * @return clave compuesta inmutable
     */
    public PagoId getId() { return new PagoId(codigoCliente, idTransaccion); }
    public Integer getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(Integer codigoCliente) { this.codigoCliente = Validaciones.enteroPositivo(codigoCliente == null ? 0 : codigoCliente, "El codigo del cliente"); }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = Validaciones.textoRequerido(formaPago, "La forma de pago"); }
    public String getIdTransaccion() { return idTransaccion; }
    public void setIdTransaccion(String idTransaccion) { this.idTransaccion = Validaciones.textoRequerido(idTransaccion, "El identificador de transaccion"); }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { if (fechaPago == null) throw new IllegalArgumentException("La fecha de pago es obligatoria."); this.fechaPago = fechaPago; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = Validaciones.decimalNoNegativo(total, "El total", false); }
}
