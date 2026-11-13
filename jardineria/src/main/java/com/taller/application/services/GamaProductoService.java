package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.application.ports.in.CrudUseCase;
import com.taller.domain.model.GamaProducto;
import com.taller.domain.ports.out.GamaProductoRepository;
import java.util.List;
import java.util.Optional;

/** Servicio de aplicación que aplica las reglas CRUD de las gamas de producto. */
public class GamaProductoService implements CrudUseCase<GamaProducto,String>{
    private final GamaProductoRepository repository;
    /** Recibe el puerto de salida; no crea ni conoce su adaptador JDBC. */
    public GamaProductoService(GamaProductoRepository repository){this.repository=repository;}
    /** Impide repetir el nombre que funciona como clave primaria. */
    @Override public void guardar(GamaProducto x){if(repository.buscarPorId(x.getGama()).isPresent())throw new ReglaNegocioException("Ya existe esa gama.");repository.guardar(x);}
    @Override public List<GamaProducto> listarTodos(){return repository.listarTodos();}
    @Override public Optional<GamaProducto> buscarPorId(String id){return repository.buscarPorId(id);}
    @Override public void actualizar(GamaProducto x){exigir(x.getGama());if(!repository.actualizar(x))throw new ReglaNegocioException("La gama no fue actualizada.");}
    @Override public void eliminar(String id){exigir(id);if(!repository.eliminarPorId(id))throw new ReglaNegocioException("La gama no fue eliminada.");}
    /** Comprueba existencia antes de actualizar o eliminar. */
    private void exigir(String id){if(repository.buscarPorId(id).isEmpty())throw new ReglaNegocioException("La gama no existe.");}
}
