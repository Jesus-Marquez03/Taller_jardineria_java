package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.domain.model.DashboardData;
import com.taller.domain.ports.out.DashboardRepository;

/**
 * Caso de uso de solo lectura que prepara los límites del Dashboard.
 * Pertenece a aplicación y delega los cálculos SQL al puerto de salida.
 */
public class DashboardService {
    private final DashboardRepository repository;
    /** Inyecta el puerto que sabe obtener el resumen. */
    public DashboardService(DashboardRepository repository){this.repository=repository;}
    /** @return resumen con límites predeterminados de 10 unidades y 8 pedidos */
    public DashboardData obtenerResumen(){return repository.obtenerResumen(10,8);}
    /**
     * Permite indicar límites y rechaza valores que producirían una consulta sin sentido.
     * @throws ReglaNegocioException si el stock es negativo o no se piden filas
     */
    public DashboardData obtenerResumen(int limiteStock,int limitePedidos){if(limiteStock<0||limitePedidos<=0)throw new ReglaNegocioException("Los limites del dashboard no son validos.");return repository.obtenerResumen(limiteStock,limitePedidos);}
}
