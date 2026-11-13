package com.taller.domain;

import com.taller.domain.model.DetallePedido;
import com.taller.domain.model.DetallePedidoId;
import com.taller.domain.model.Pago;
import com.taller.domain.model.Producto;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Pruebas unitarias que comprueban reglas básicas sin conectarse a MySQL. */
class ModeloValidacionTest {
    @Test
    void productoNoPermiteStockNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Producto(
                "P-1", "Planta", "Ornamentales", null, null, null,
                -1, new BigDecimal("10.00"), null));
    }

    @Test
    void detalleNoPermiteCantidadCero() {
        assertThrows(IllegalArgumentException.class, () -> new DetallePedido(
                1, "P-1", 0, new BigDecimal("10.00"), 1));
    }

    @Test
    void pagoNoPermiteTotalNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Pago(
                1, "Tarjeta", "TX-1", LocalDate.of(2026, 1, 1), new BigDecimal("-0.01")));
    }

    @Test
    void identificadorCompuestoConservaAmbasPartes() {
        DetallePedidoId id = new DetallePedidoId(7, "P-20");
        assertEquals(7, id.codigoPedido());
        assertEquals("P-20", id.codigoProducto());
    }
}
