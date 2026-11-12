package com.taller.domain.ports.out;

import com.taller.domain.model.Empleado;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository {
    void guardar(Empleado empleado);
    List<Empleado> listarTodos();
    Optional<Empleado> buscarPorId(Integer codigoEmpleado);
    boolean actualizar(Empleado empleado);
    boolean eliminarPorId(Integer codigoEmpleado);
}
