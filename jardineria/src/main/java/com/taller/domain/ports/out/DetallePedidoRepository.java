package com.taller.domain.ports.out;

import com.taller.domain.model.DetallePedido;
import com.taller.domain.model.DetallePedidoId;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las líneas de pedido.
 * Recibe {@link DetallePedidoId} porque la tabla tiene clave primaria compuesta.
 */
public interface DetallePedidoRepository {
    /** Inserta una línea de pedido. */
    void guardar(DetallePedido detallePedido);
    /** @return todas las líneas */
    List<DetallePedido> listarTodos();
    /** @return el detalle correspondiente a las dos partes de la clave, o vacío */
    Optional<DetallePedido> buscarPorId(DetallePedidoId id);
    /** @return si se actualizó una fila */
    boolean actualizar(DetallePedido detallePedido);
    /** @return si se eliminó una fila */
    boolean eliminarPorId(DetallePedidoId id);
}
