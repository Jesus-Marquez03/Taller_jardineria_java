package com.taller.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
