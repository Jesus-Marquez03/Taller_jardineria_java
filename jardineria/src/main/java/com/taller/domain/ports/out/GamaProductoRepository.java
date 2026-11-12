package com.taller.domain.ports.out;

import com.taller.domain.model.GamaProducto;
import java.util.List;
import java.util.Optional;

public interface GamaProductoRepository {
    void guardar(GamaProducto gamaProducto);
    List<GamaProducto> listarTodos();
    Optional<GamaProducto> buscarPorId(String gama);
    boolean actualizar(GamaProducto gamaProducto);
    boolean eliminarPorId(String gama);
}
