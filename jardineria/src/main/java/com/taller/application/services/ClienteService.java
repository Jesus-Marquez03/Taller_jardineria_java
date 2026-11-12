package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.application.ports.in.CrudUseCase;
import com.taller.domain.model.Cliente;
import com.taller.domain.ports.out.ClienteRepository;
import com.taller.domain.ports.out.EmpleadoRepository;
import java.util.List;
import java.util.Optional;

public class ClienteService implements CrudUseCase<Cliente,Integer>{
    private final ClienteRepository repository;private final EmpleadoRepository empleados;
    public ClienteService(ClienteRepository repository,EmpleadoRepository empleados){this.repository=repository;this.empleados=empleados;}
    @Override public void guardar(Cliente x){if(repository.buscarPorId(x.getCodigoCliente()).isPresent())throw new ReglaNegocioException("Ya existe ese cliente.");validar(x);repository.guardar(x);}
    @Override public List<Cliente> listarTodos(){return repository.listarTodos();}
    @Override public Optional<Cliente> buscarPorId(Integer id){return repository.buscarPorId(id);}
    @Override public void actualizar(Cliente x){exigir(x.getCodigoCliente());validar(x);if(!repository.actualizar(x))throw new ReglaNegocioException("El cliente no fue actualizado.");}
    @Override public void eliminar(Integer id){exigir(id);if(!repository.eliminarPorId(id))throw new ReglaNegocioException("El cliente no fue eliminado.");}
    private void validar(Cliente x){if(x.getCodigoEmpleadoRepVentas()!=null&&empleados.buscarPorId(x.getCodigoEmpleadoRepVentas()).isEmpty())throw new ReglaNegocioException("El representante de ventas no existe.");}
    private void exigir(Integer id){if(repository.buscarPorId(id).isEmpty())throw new ReglaNegocioException("El cliente no existe.");}
}
