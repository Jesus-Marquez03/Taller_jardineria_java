package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.application.ports.in.CrudUseCase;
import com.taller.domain.model.Pago;
import com.taller.domain.model.PagoId;
import com.taller.domain.ports.out.ClienteRepository;
import com.taller.domain.ports.out.PagoRepository;
import java.util.List;
import java.util.Optional;

public class PagoService implements CrudUseCase<Pago,PagoId>{
    private final PagoRepository repository;private final ClienteRepository clientes;
    public PagoService(PagoRepository repository,ClienteRepository clientes){this.repository=repository;this.clientes=clientes;}
    @Override public void guardar(Pago x){if(repository.buscarPorId(x.getId()).isPresent())throw new ReglaNegocioException("Ya existe esa transaccion para el cliente.");validar(x);repository.guardar(x);}
    @Override public List<Pago> listarTodos(){return repository.listarTodos();}
    @Override public Optional<Pago> buscarPorId(PagoId id){return repository.buscarPorId(id);}
    @Override public void actualizar(Pago x){exigir(x.getId());validar(x);if(!repository.actualizar(x))throw new ReglaNegocioException("El pago no fue actualizado.");}
    @Override public void eliminar(PagoId id){exigir(id);if(!repository.eliminarPorId(id))throw new ReglaNegocioException("El pago no fue eliminado.");}
    private void validar(Pago x){if(clientes.buscarPorId(x.getCodigoCliente()).isEmpty())throw new ReglaNegocioException("El cliente seleccionado no existe.");}
    private void exigir(PagoId id){if(repository.buscarPorId(id).isEmpty())throw new ReglaNegocioException("El pago no existe.");}
}
