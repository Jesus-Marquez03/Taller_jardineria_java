package com.taller.domain.ports.out;

import com.taller.domain.model.GamaProducto;
import java.util.List;
import java.util.Optional;

/** Puerto de salida para consultar y modificar gamas de productos. */
public interface GamaProductoRepository {
    /** Inserta una gama nueva. */
    void guardar(GamaProducto gamaProducto);
    /** @return todas las gamas */
    List<GamaProducto> listarTodos();
    /** @return la gama buscada o vacío */
    Optional<GamaProducto> buscarPorId(String gama);
    /** @return si se actualizó una fila */
    boolean actualizar(GamaProducto gamaProducto);
    /** @return si se eliminó una fila */
    boolean eliminarPorId(String gama);
}
