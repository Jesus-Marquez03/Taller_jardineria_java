package com.taller.domain.ports.out;

import com.taller.domain.model.Producto;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida del catálogo de productos.
 * Además del CRUD incluye la consulta de inventario crítico que necesita el negocio.
 */
public interface ProductoRepository {
    /** Inserta un producto. */
    void guardar(Producto producto);
    /** @return todos los productos */
    List<Producto> listarTodos();
    /** @return el producto o vacío si no existe */
    Optional<Producto> buscarPorId(String codigoProducto);
    /**
     * Busca productos cuya existencia está por debajo o igual al límite.
     * @param limite máximo de unidades considerado crítico
     */
    List<Producto> listarConStockMenorOIgual(int limite);
    /** @return si se actualizó una fila */
    boolean actualizar(Producto producto);
    /** @return si se eliminó una fila */
    boolean eliminarPorId(String codigoProducto);
}
