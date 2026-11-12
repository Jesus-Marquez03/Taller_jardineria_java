package com.taller.integration;

import com.taller.domain.model.*;
import com.taller.infrastructure.config.ApplicationContainer;
import com.taller.infrastructure.config.DatabaseConfig;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcIntegrationIT {
    @Test
    void crudDeLasOchoEntidadesRespetaRelaciones() {
        ApplicationContainer app = new ApplicationContainer(DatabaseConfig.getInstance());
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        int seed = 700_000 + Math.abs(suffix.hashCode() % 90_000);
        String oficinaId = "T-" + suffix.substring(0, 6);
        int empleadoId = seed;
        int clienteId = seed + 100_000;
        int pedidoId = seed + 200_000;
        String gamaId = "Prueba-" + suffix;
        String productoId = "T-" + suffix;
        DetallePedidoId detalleId = new DetallePedidoId(pedidoId, productoId);
        PagoId pagoId = new PagoId(clienteId, "TX-" + suffix);

        try {
            Oficina oficina = new Oficina(oficinaId, "Ciudad prueba", "Colombia", null,
                    "110111", "5550000", "Direccion temporal", null);
            app.oficinas().guardar(oficina);
            Empleado empleado = new Empleado(empleadoId, "Empleado", "Temporal", null,
                    "100", "temporal@example.test", oficinaId, null, "Pruebas");
            app.empleados().guardar(empleado);
            Cliente cliente = new Cliente(clienteId, "Cliente temporal", null, null, "5551111", "5551112",
                    "Direccion temporal", null, "Bogota", null, "Colombia", "110111", empleadoId,
                    new BigDecimal("1000.00"));
            app.clientes().guardar(cliente);
            app.gamas().guardar(new GamaProducto(gamaId, "Dato temporal de integracion", null, null));
            Producto producto = new Producto(productoId, "Producto temporal", gamaId, null, null,
                    "Se elimina al terminar", 12, new BigDecimal("25.50"), new BigDecimal("10.00"));
            app.productos().guardar(producto);
            Pedido pedido = new Pedido(pedidoId, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 15),
                    null, "Pendiente", "Prueba temporal", clienteId);
            app.pedidos().guardar(pedido);
            DetallePedido detalle = new DetallePedido(pedidoId, productoId, 2, new BigDecimal("25.50"), 1);
            app.detalles().guardar(detalle);
            Pago pago = new Pago(clienteId, "Transferencia", pagoId.idTransaccion(),
                    LocalDate.of(2026, 1, 11), new BigDecimal("51.00"));
            app.pagos().guardar(pago);

            assertTrue(app.oficinas().buscarPorId(oficinaId).isPresent());
            assertTrue(app.empleados().buscarPorId(empleadoId).isPresent());
            assertTrue(app.clientes().buscarPorId(clienteId).isPresent());
            assertTrue(app.gamas().buscarPorId(gamaId).isPresent());
            assertTrue(app.productos().buscarPorId(productoId).isPresent());
            assertTrue(app.pedidos().buscarPorId(pedidoId).isPresent());
            assertTrue(app.detalles().buscarPorId(detalleId).isPresent());
            assertTrue(app.pagos().buscarPorId(pagoId).isPresent());

            oficina.setCiudad("Ciudad actualizada"); app.oficinas().actualizar(oficina);
            empleado.setPuesto("Puesto actualizado"); app.empleados().actualizar(empleado);
            cliente.setLimiteCredito(new BigDecimal("1200.00")); app.clientes().actualizar(cliente);
            GamaProducto gama = app.gamas().buscarPorId(gamaId).orElseThrow(); gama.setDescripcionTexto("Actualizada"); app.gamas().actualizar(gama);
            producto.setCantidadEnStock(9); app.productos().actualizar(producto);
            pedido.setComentarios("Actualizado"); app.pedidos().actualizar(pedido);
            detalle.setCantidad(3); app.detalles().actualizar(detalle);
            pago.setTotal(new BigDecimal("76.50")); app.pagos().actualizar(pago);

            assertEquals("Ciudad actualizada", app.oficinas().buscarPorId(oficinaId).orElseThrow().getCiudad());
            assertEquals(9, app.productos().buscarPorId(productoId).orElseThrow().getCantidadEnStock());
            assertEquals(new BigDecimal("76.50"), app.pagos().buscarPorId(pagoId).orElseThrow().getTotal());
            assertTrue(app.dashboard().obtenerResumen().totalProductos() >= 1);
        } finally {
            app.pagos().buscarPorId(pagoId).ifPresent(x -> app.pagos().eliminar(pagoId));
            app.detalles().buscarPorId(detalleId).ifPresent(x -> app.detalles().eliminar(detalleId));
            app.pedidos().buscarPorId(pedidoId).ifPresent(x -> app.pedidos().eliminar(pedidoId));
            app.productos().buscarPorId(productoId).ifPresent(x -> app.productos().eliminar(productoId));
            app.gamas().buscarPorId(gamaId).ifPresent(x -> app.gamas().eliminar(gamaId));
            app.clientes().buscarPorId(clienteId).ifPresent(x -> app.clientes().eliminar(clienteId));
            app.empleados().buscarPorId(empleadoId).ifPresent(x -> app.empleados().eliminar(empleadoId));
            app.oficinas().buscarPorId(oficinaId).ifPresent(x -> app.oficinas().eliminar(oficinaId));
        }
    }
}
