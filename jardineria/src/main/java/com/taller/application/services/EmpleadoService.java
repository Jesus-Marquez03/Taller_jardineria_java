package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.application.ports.in.CrudUseCase;
import com.taller.domain.model.Empleado;
import com.taller.domain.ports.out.EmpleadoRepository;
import com.taller.domain.ports.out.OficinaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Coordina los casos de uso de empleados.
 *
 * <p>Usa dos puertos: uno persiste empleados y otro comprueba oficinas. Su
 * responsabilidad es validar duplicados, oficina y jefe antes de delegar el
 * CRUD al adaptador de persistencia.</p>
 */
public class EmpleadoService implements CrudUseCase<Empleado,Integer>{
    private final EmpleadoRepository repository; private final OficinaRepository oficinas;
    /** Recibe por constructor los repositorios que necesita para sus reglas. */
    public EmpleadoService(EmpleadoRepository repository,OficinaRepository oficinas){this.repository=repository;this.oficinas=oficinas;}
    /** Rechaza códigos repetidos, valida referencias y finalmente guarda. */
    @Override public void guardar(Empleado x){if(repository.buscarPorId(x.getCodigoEmpleado()).isPresent())throw new ReglaNegocioException("Ya existe ese empleado.");validarReferencias(x);repository.guardar(x);}
    @Override public List<Empleado> listarTodos(){return repository.listarTodos();}
    @Override public Optional<Empleado> buscarPorId(Integer id){return repository.buscarPorId(id);}
    @Override public void actualizar(Empleado x){exigir(x.getCodigoEmpleado());validarReferencias(x);if(!repository.actualizar(x))throw new ReglaNegocioException("El empleado no fue actualizado.");}
    @Override public void eliminar(Integer id){exigir(id);if(!repository.eliminarPorId(id))throw new ReglaNegocioException("El empleado no fue eliminado.");}
    /** Comprueba la clave foránea de oficina y la relación opcional con el jefe. */
    private void validarReferencias(Empleado x){if(oficinas.buscarPorId(x.getCodigoOficina()).isEmpty())throw new ReglaNegocioException("La oficina seleccionada no existe.");if(x.getCodigoJefe()!=null){if(x.getCodigoJefe().equals(x.getCodigoEmpleado()))throw new ReglaNegocioException("Un empleado no puede ser su propio jefe.");if(repository.buscarPorId(x.getCodigoJefe()).isEmpty())throw new ReglaNegocioException("El jefe seleccionado no existe.");}}
    /** Exige que exista el empleado antes de modificarlo o eliminarlo. */
    private void exigir(Integer id){if(repository.buscarPorId(id).isEmpty())throw new ReglaNegocioException("El empleado no existe.");}
}
