package com.taller.domain.exception;

/**
 * Comunica hacia las capas superiores que una operación de persistencia falló.
 *
 * <p>Es una excepción no comprobada: conserva la causa {@code SQLException},
 * pero evita que la interfaz dependa directamente de detalles de JDBC.</p>
 */
public class PersistenciaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** Crea un error comprensible sin perder la excepción técnica original. */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
