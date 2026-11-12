package com.taller.domain.ports.out;

import com.taller.domain.model.DashboardData;

public interface DashboardRepository {
    DashboardData obtenerResumen(int limiteStock, int limitePedidos);
}
