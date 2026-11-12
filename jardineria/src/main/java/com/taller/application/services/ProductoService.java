package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.application.ports.in.CrudUseCase;
import com.taller.domain.model.Producto;
import com.taller.domain.ports.out.GamaProductoRepository;
import com.taller.domain.ports.out.ProductoRepository;
import java.util.List;
import java.util.Optional;

public class ProductoService implements CrudUseCase<Producto,String>{
    private final ProductoRepository repository;private final GamaProductoRepository gamas;
    public ProductoService(ProductoRepository repository,GamaProductoRepository gamas){this.repository=repository;this.gamas=gamas;}
    @Override public void guardar(Producto x){if(repository.buscarPorId(x.getCodigoProducto()).isPresent())throw new ReglaNegocioException("Ya existe ese producto.");validar(x);repository.guardar(x);}
    @Override public List<Producto> listarTodos(){return repository.listarTodos();}
    public List<Producto> listarStockCritico(int limite){if(limite<0)throw new ReglaNegocioException("El limite de stock no puede ser negativo.");return repository.listarConStockMenorOIgual(limite);}
    @Override public Optional<Producto> buscarPorId(String id){return repository.buscarPorId(id);}
    @Override public void actualizar(Producto x){exigir(x.getCodigoProducto());validar(x);if(!repository.actualizar(x))throw new ReglaNegocioException("El producto no fue actualizado.");}
    @Override public void eliminar(String id){exigir(id);if(!repository.eliminarPorId(id))throw new ReglaNegocioException("El producto no fue eliminado.");}
    private void validar(Producto x){if(gamas.buscarPorId(x.getGama()).isEmpty())throw new ReglaNegocioException("La gama seleccionada no existe.");}
    private void exigir(String id){if(repository.buscarPorId(id).isEmpty())throw new ReglaNegocioException("El producto no existe.");}
}
