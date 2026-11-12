package com.taller.domain.model;

import java.math.BigDecimal;

public class Cliente {
    private Integer codigoCliente;
    private String nombreCliente;
    private String nombreContacto;
    private String apellidoContacto;
    private String telefono;
    private String fax;
    private String lineaDireccion1;
    private String lineaDireccion2;
    private String ciudad;
    private String region;
    private String pais;
    private String codigoPostal;
    private Integer codigoEmpleadoRepVentas;
    private BigDecimal limiteCredito;

    public Cliente(Integer codigoCliente, String nombreCliente, String nombreContacto, String apellidoContacto,
                   String telefono, String fax, String lineaDireccion1, String lineaDireccion2, String ciudad,
                   String region, String pais, String codigoPostal, Integer codigoEmpleadoRepVentas,
                   BigDecimal limiteCredito) {
        setCodigoCliente(codigoCliente);
        setNombreCliente(nombreCliente);
        this.nombreContacto = nombreContacto;
        this.apellidoContacto = apellidoContacto;
        setTelefono(telefono);
        setFax(fax);
        setLineaDireccion1(lineaDireccion1);
        this.lineaDireccion2 = lineaDireccion2;
        setCiudad(ciudad);
        this.region = region;
        this.pais = pais;
        this.codigoPostal = codigoPostal;
        this.codigoEmpleadoRepVentas = codigoEmpleadoRepVentas;
        setLimiteCredito(limiteCredito);
    }

    public Integer getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(Integer codigoCliente) { this.codigoCliente = Validaciones.enteroPositivo(codigoCliente == null ? 0 : codigoCliente, "El codigo de cliente"); }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = Validaciones.textoRequerido(nombreCliente, "El nombre del cliente"); }
    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }
    public String getApellidoContacto() { return apellidoContacto; }
    public void setApellidoContacto(String apellidoContacto) { this.apellidoContacto = apellidoContacto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = Validaciones.textoRequerido(telefono, "El telefono"); }
    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = Validaciones.textoRequerido(fax, "El fax"); }
    public String getLineaDireccion1() { return lineaDireccion1; }
    public void setLineaDireccion1(String lineaDireccion1) { this.lineaDireccion1 = Validaciones.textoRequerido(lineaDireccion1, "La direccion"); }
    public String getLineaDireccion2() { return lineaDireccion2; }
    public void setLineaDireccion2(String lineaDireccion2) { this.lineaDireccion2 = lineaDireccion2; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = Validaciones.textoRequerido(ciudad, "La ciudad"); }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public Integer getCodigoEmpleadoRepVentas() { return codigoEmpleadoRepVentas; }
    public void setCodigoEmpleadoRepVentas(Integer codigoEmpleadoRepVentas) { this.codigoEmpleadoRepVentas = codigoEmpleadoRepVentas; }
    public BigDecimal getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito = Validaciones.decimalNoNegativo(limiteCredito, "El limite de credito", true); }

    @Override public String toString() { return codigoCliente + " - " + nombreCliente; }
}
