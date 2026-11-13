package com.taller.domain.model;

/**
 * Representa una categoría o gama que agrupa productos semejantes.
 *
 * <p>Es una entidad de dominio independiente de la base de datos. La gama es
 * su clave primaria; las descripciones y la imagen son información opcional.</p>
 */
public class GamaProducto {
    private String gama;
    private String descripcionTexto;
    private String descripcionHtml;
    private String imagen;

    /** Crea la gama y exige que su identificador tenga texto. */
    public GamaProducto(String gama, String descripcionTexto, String descripcionHtml, String imagen) {
        setGama(gama);
        this.descripcionTexto = descripcionTexto;
        this.descripcionHtml = descripcionHtml;
        this.imagen = imagen;
    }

    public String getGama() { return gama; }
    public void setGama(String gama) { this.gama = Validaciones.textoRequerido(gama, "La gama"); }
    public String getDescripcionTexto() { return descripcionTexto; }
    public void setDescripcionTexto(String descripcionTexto) { this.descripcionTexto = descripcionTexto; }
    public String getDescripcionHtml() { return descripcionHtml; }
    public void setDescripcionHtml(String descripcionHtml) { this.descripcionHtml = descripcionHtml; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    @Override public String toString() { return gama; }
}
