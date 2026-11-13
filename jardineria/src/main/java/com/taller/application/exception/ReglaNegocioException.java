package com.taller.application.exception;

/**
 * Indica que una operación viola una regla comprensible del negocio.
 * Los servicios la lanzan y la interfaz muestra su mensaje al usuario.
 */
public class ReglaNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** @param mensaje explicación clara de la regla que no se cumplió */
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
