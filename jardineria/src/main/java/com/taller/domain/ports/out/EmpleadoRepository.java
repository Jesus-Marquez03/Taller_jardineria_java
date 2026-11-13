package com.taller.domain.ports.out;

import com.taller.domain.model.Empleado;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de empleados.
 * La capa de aplicación usa esta interfaz y desconoce qué base de datos hay detrás.
 */
public interface EmpleadoRepository {
    /** Inserta un empleado nuevo. */
    void guardar(Empleado empleado);
    /** @return todos los empleados */
    List<Empleado> listarTodos();
    /** @return el empleado o un {@link Optional} vacío si no existe */
    Optional<Empleado> buscarPorId(Integer codigoEmpleado);
    /** @return si la actualización afectó una fila */
    boolean actualizar(Empleado empleado);
    /** @return si la eliminación afectó una fila */
    boolean eliminarPorId(Integer codigoEmpleado);
}
