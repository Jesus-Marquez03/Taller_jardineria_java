package com.taller.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Transporta en un solo objeto todos los resultados que necesita el Dashboard.
 *
 * <p>Es un {@code record} de dominio: sus datos son inmutables y Java genera
 * los métodos de acceso. La lista contiene los pedidos recientes y el mapa
 * relaciona cada estado con su cantidad.</p>
 */
public record DashboardData(
        BigDecimal totalIngresos,
        int cantidadPagos,
        int pedidosPendientes,
        int pedidosEntregados,
        int totalClientes,
        int productosStockCritico,
        int totalProductos,
        List<Pedido> ultimosPedidos,
        Map<String,Integer> pedidosPorEstado) {
}
