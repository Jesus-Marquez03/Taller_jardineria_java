package com.taller.domain.ports.out;

import com.taller.domain.model.Oficina;
import java.util.List;
import java.util.Optional;

public interface OficinaRepository {
    void guardar(Oficina oficina);
    List<Oficina> listarTodos();
    Optional<Oficina> buscarPorId(String codigoOficina);
    boolean actualizar(Oficina oficina);
    boolean eliminarPorId(String codigoOficina);
}
