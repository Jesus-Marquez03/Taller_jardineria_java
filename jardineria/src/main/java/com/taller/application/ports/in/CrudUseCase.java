package com.taller.application.ports.in;

import java.util.List;
import java.util.Optional;

public interface CrudUseCase<T, ID> {
    void guardar(T entidad);
    List<T> listarTodos();
    Optional<T> buscarPorId(ID id);
    void actualizar(T entidad);
    void eliminar(ID id);
}
