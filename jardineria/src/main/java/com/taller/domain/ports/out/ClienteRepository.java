package com.taller.domain.ports.out;

import com.taller.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    void guardar(Cliente cliente);
    List<Cliente> listarTodos();
    Optional<Cliente> buscarPorId(Integer codigoCliente);
    boolean actualizar(Cliente cliente);
    boolean eliminarPorId(Integer codigoCliente);
}
