package com.taller.domain.model;

/**
 * Representa una oficina física de la empresa de jardinería.
 *
 * <p>Sus atributos privados guardan la ubicación y los datos de contacto. Esta
 * clase pertenece al dominio: describe el negocio, pero no conoce JDBC, MySQL
 * ni Swing. Los getters permiten leer el estado del objeto y los setters lo
 * modifican de forma controlada, lo que demuestra encapsulamiento.</p>
 */
public class Oficina {
    private String codigoOficina;
    private String ciudad;
    private String pais;
    private String region;
    private String codigoPostal;
    private String telefono;
    private String lineaDireccion1;
    private String lineaDireccion2;

    /**
     * Construye una oficina y pasa los campos obligatorios por sus setters para
     * que el objeto nunca nazca con datos inválidos. Región y segunda dirección
     * pueden ser {@code null} porque la tabla permite esos valores opcionales.
     */
    public Oficina(String codigoOficina, String ciudad, String pais, String region,
                   String codigoPostal, String telefono, String lineaDireccion1, String lineaDireccion2) {
        setCodigoOficina(codigoOficina);
        setCiudad(ciudad);
        setPais(pais);
        this.region = region;
        setCodigoPostal(codigoPostal);
        setTelefono(telefono);
        setLineaDireccion1(lineaDireccion1);
        this.lineaDireccion2 = lineaDireccion2;
    }

    public String getCodigoOficina() { return codigoOficina; }
    public void setCodigoOficina(String codigoOficina) { this.codigoOficina = Validaciones.textoRequerido(codigoOficina, "El codigo de oficina"); }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = Validaciones.textoRequerido(ciudad, "La ciudad"); }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = Validaciones.textoRequerido(pais, "El pais"); }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = Validaciones.textoRequerido(codigoPostal, "El codigo postal"); }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = Validaciones.textoRequerido(telefono, "El telefono"); }
    public String getLineaDireccion1() { return lineaDireccion1; }
    public void setLineaDireccion1(String lineaDireccion1) { this.lineaDireccion1 = Validaciones.textoRequerido(lineaDireccion1, "La direccion"); }
    public String getLineaDireccion2() { return lineaDireccion2; }
    public void setLineaDireccion2(String lineaDireccion2) { this.lineaDireccion2 = lineaDireccion2; }

    @Override public String toString() { return codigoOficina + " - " + ciudad; }
}
