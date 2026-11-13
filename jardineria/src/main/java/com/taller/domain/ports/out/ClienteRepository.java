package com.taller.domain.ports.out;

import com.taller.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida que expresa las operaciones necesarias para guardar clientes.
 * Su implementación JDBC es un adaptador intercambiable de infraestructura.
 */
public interface ClienteRepository {
    /** Inserta un cliente nuevo. */
    void guardar(Cliente cliente);
    /** @return todos los clientes */
    List<Cliente> listarTodos();
    /** @return el cliente o vacío cuando el código no está registrado */
    Optional<Cliente> buscarPorId(Integer codigoCliente);
    /** @return si se actualizó una fila */
    boolean actualizar(Cliente cliente);
    /** @return si se eliminó una fila */
    boolean eliminarPorId(Integer codigoCliente);
}
