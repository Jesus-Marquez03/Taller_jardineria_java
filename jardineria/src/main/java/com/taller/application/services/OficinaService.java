package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.application.ports.in.CrudUseCase;
import com.taller.domain.model.Oficina;
import com.taller.domain.ports.out.OficinaRepository;
import java.util.List;
import java.util.Optional;

public class OficinaService implements CrudUseCase<Oficina, String> {
    private final OficinaRepository repository;
    public OficinaService(OficinaRepository repository){this.repository=repository;}
    @Override public void guardar(Oficina x){siExiste(x.getCodigoOficina());repository.guardar(x);}
    @Override public List<Oficina> listarTodos(){return repository.listarTodos();}
    @Override public Optional<Oficina> buscarPorId(String id){return repository.buscarPorId(id);}
    @Override public void actualizar(Oficina x){siNoExiste(x.getCodigoOficina());if(!repository.actualizar(x))throw new ReglaNegocioException("La oficina no fue actualizada.");}
    @Override public void eliminar(String id){siNoExiste(id);if(!repository.eliminarPorId(id))throw new ReglaNegocioException("La oficina no fue eliminada.");}
    private void siExiste(String id){if(repository.buscarPorId(id).isPresent())throw new ReglaNegocioException("Ya existe una oficina con ese codigo.");}
    private void siNoExiste(String id){if(repository.buscarPorId(id).isEmpty())throw new ReglaNegocioException("La oficina no existe.");}
}
