package com.taller.domain.ports.out;

import com.taller.domain.model.DashboardData;

/**
 * Puerto de lectura para obtener los indicadores del Dashboard.
 * Separa lo que necesita la aplicación de las consultas SQL que lo calculan.
 */
public interface DashboardRepository {
    /**
     * Reúne ingresos, pedidos, clientes, inventario y estados.
     * @param limiteStock cantidad máxima que se considera stock crítico
     * @param limitePedidos cantidad de pedidos recientes
     * @return resumen listo para mostrar
     */
    DashboardData obtenerResumen(int limiteStock, int limitePedidos);
}
