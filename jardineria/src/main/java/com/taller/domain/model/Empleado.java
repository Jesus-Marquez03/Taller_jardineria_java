package com.taller.domain.model;

public class Empleado {
    private Integer codigoEmpleado;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String extension;
    private String email;
    private String codigoOficina;
    private Integer codigoJefe;
    private String puesto;

    public Empleado(Integer codigoEmpleado, String nombre, String apellido1, String apellido2,
                    String extension, String email, String codigoOficina, Integer codigoJefe, String puesto) {
        setCodigoEmpleado(codigoEmpleado);
        setNombre(nombre);
        setApellido1(apellido1);
        this.apellido2 = apellido2;
        setExtension(extension);
        setEmail(email);
        setCodigoOficina(codigoOficina);
        this.codigoJefe = codigoJefe;
        this.puesto = puesto;
    }

    public Integer getCodigoEmpleado() { return codigoEmpleado; }
    public void setCodigoEmpleado(Integer codigoEmpleado) { this.codigoEmpleado = Validaciones.enteroPositivo(codigoEmpleado == null ? 0 : codigoEmpleado, "El codigo de empleado"); }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Validaciones.textoRequerido(nombre, "El nombre"); }
    public String getApellido1() { return apellido1; }
    public void setApellido1(String apellido1) { this.apellido1 = Validaciones.textoRequerido(apellido1, "El primer apellido"); }
    public String getApellido2() { return apellido2; }
    public void setApellido2(String apellido2) { this.apellido2 = apellido2; }
    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = Validaciones.textoRequerido(extension, "La extension"); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = Validaciones.textoRequerido(email, "El correo"); }
    public String getCodigoOficina() { return codigoOficina; }
    public void setCodigoOficina(String codigoOficina) { this.codigoOficina = Validaciones.textoRequerido(codigoOficina, "La oficina"); }
    public Integer getCodigoJefe() { return codigoJefe; }
    public void setCodigoJefe(Integer codigoJefe) { this.codigoJefe = codigoJefe; }
    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    @Override public String toString() { return codigoEmpleado + " - " + nombre + " " + apellido1; }
}
