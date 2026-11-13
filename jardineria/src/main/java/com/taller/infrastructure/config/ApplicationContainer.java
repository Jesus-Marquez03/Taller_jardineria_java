package com.taller.infrastructure.config;

import com.taller.application.services.*;
import com.taller.infrastructure.persistence.*;

/**
 * Crea y conecta manualmente las piezas de la aplicación.
 *
 * <p>Esta clase de infraestructura es la raíz de composición. Primero crea los
 * adaptadores JDBC y luego los inyecta en los servicios que dependen de sus
 * interfaces. Así las clases no buscan dependencias globales ni conocen cómo
 * construirlas. Los métodos finales entregan a Swing los casos de uso listos.</p>
 */
public final class ApplicationContainer {
    private final OficinaService oficinaService;
    private final EmpleadoService empleadoService;
    private final ClienteService clienteService;
    private final GamaProductoService gamaProductoService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final DetallePedidoService detallePedidoService;
    private final PagoService pagoService;
    private final DashboardService dashboardService;

    /**
     * Construye el grafo completo de dependencias a partir de la configuración.
     * @param config configuración compartida que abre conexiones JDBC
     */
    public ApplicationContainer(DatabaseConfig config) {
        var oficinas = new OficinaJdbcRepository(config);
        var empleados = new EmpleadoJdbcRepository(config);
        var clientes = new ClienteJdbcRepository(config);
        var gamas = new GamaProductoJdbcRepository(config);
        var productos = new ProductoJdbcRepository(config);
        var pedidos = new PedidoJdbcRepository(config);
        var detalles = new DetallePedidoJdbcRepository(config);
        var pagos = new PagoJdbcRepository(config);
        var dashboard = new DashboardJdbcRepository(config);

        oficinaService = new OficinaService(oficinas);
        empleadoService = new EmpleadoService(empleados, oficinas);
        clienteService = new ClienteService(clientes, empleados);
        gamaProductoService = new GamaProductoService(gamas);
        productoService = new ProductoService(productos, gamas);
        pedidoService = new PedidoService(pedidos, clientes);
        detallePedidoService = new DetallePedidoService(detalles, pedidos, productos);
        pagoService = new PagoService(pagos, clientes);
        dashboardService = new DashboardService(dashboard);
    }

    /** Los siguientes accesores exponen servicios, nunca repositorios JDBC, a la UI. */
    public OficinaService oficinas() { return oficinaService; }
    public EmpleadoService empleados() { return empleadoService; }
    public ClienteService clientes() { return clienteService; }
    public GamaProductoService gamas() { return gamaProductoService; }
    public ProductoService productos() { return productoService; }
    public PedidoService pedidos() { return pedidoService; }
    public DetallePedidoService detalles() { return detallePedidoService; }
    public PagoService pagos() { return pagoService; }
    public DashboardService dashboard() { return dashboardService; }
}
