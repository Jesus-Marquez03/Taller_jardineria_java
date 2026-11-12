package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.domain.model.DashboardData;
import com.taller.domain.ports.out.DashboardRepository;

public class DashboardService {
    private final DashboardRepository repository;
    public DashboardService(DashboardRepository repository){this.repository=repository;}
    public DashboardData obtenerResumen(){return repository.obtenerResumen(10,8);}
    public DashboardData obtenerResumen(int limiteStock,int limitePedidos){if(limiteStock<0||limitePedidos<=0)throw new ReglaNegocioException("Los limites del dashboard no son validos.");return repository.obtenerResumen(limiteStock,limitePedidos);}
}
