package com.taller.domain.model;

import java.math.BigDecimal;

final class Validaciones {
    private Validaciones() {
    }

    static String textoRequerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor.trim();
    }

    static int enteroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser mayor que cero.");
        }
        return valor;
    }

    static int enteroNoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo.");
        }
        return valor;
    }

    static BigDecimal decimalNoNegativo(BigDecimal valor, String campo, boolean admiteNull) {
        if (valor == null && admiteNull) {
            return null;
        }
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo.");
        }
        return valor;
    }
}
