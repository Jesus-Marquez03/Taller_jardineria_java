package com.taller.domain.model;

import java.math.BigDecimal;

/**
 * Reúne validaciones pequeñas compartidas por las entidades del dominio.
 *
 * <p>Su constructor es privado porque no representa un objeto del negocio: es
 * una clase auxiliar con métodos estáticos. Mantener estas reglas aquí evita
 * repetirlas y deja cada entidad responsable de conservar un estado válido.</p>
 */
final class Validaciones {
    private Validaciones() {
    }

    /**
     * Rechaza texto nulo o vacío y elimina espacios exteriores.
     * @return el texto limpio
     * @throws IllegalArgumentException si falta el dato obligatorio
     */
    static String textoRequerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor.trim();
    }

    /** Exige un entero mayor que cero, usado principalmente en identificadores. */
    static int enteroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser mayor que cero.");
        }
        return valor;
    }

    /** Permite cero, pero rechaza números negativos como un stock imposible. */
    static int enteroNoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo.");
        }
        return valor;
    }

    /**
     * Valida cantidades decimales y respeta si la columna es opcional.
     * @param admiteNull indica si {@code null} es un valor permitido
     * @return el mismo decimal validado, o {@code null} cuando está permitido
     */
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
