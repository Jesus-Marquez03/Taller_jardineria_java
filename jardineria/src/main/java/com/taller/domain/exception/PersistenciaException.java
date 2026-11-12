package com.taller.domain.exception;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
