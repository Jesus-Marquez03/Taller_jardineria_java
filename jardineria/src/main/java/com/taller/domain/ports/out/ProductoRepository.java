package com.taller.domain.ports.out;

import com.taller.domain.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    void guardar(Producto producto);
    List<Producto> listarTodos();
    Optional<Producto> buscarPorId(String codigoProducto);
    List<Producto> listarConStockMenorOIgual(int limite);
    boolean actualizar(Producto producto);
    boolean eliminarPorId(String codigoProducto);
}
