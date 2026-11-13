package com.taller.domain.ports.out;

import com.taller.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

/** Puerto de salida para persistir pedidos y consultar los más recientes. */
public interface PedidoRepository {
    /** Inserta un pedido. */
    void guardar(Pedido pedido);
    /** @return todos los pedidos */
    List<Pedido> listarTodos();
    /** @return el pedido o vacío */
    Optional<Pedido> buscarPorId(Integer codigoPedido);
    /** @param limite cantidad máxima de pedidos recientes que se necesitan */
    List<Pedido> listarUltimos(int limite);
    /** @return si se actualizó una fila */
    boolean actualizar(Pedido pedido);
    /** @return si se eliminó una fila */
    boolean eliminarPorId(Integer codigoPedido);
}
