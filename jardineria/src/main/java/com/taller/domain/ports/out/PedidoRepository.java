package com.taller.domain.ports.out;

import com.taller.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    void guardar(Pedido pedido);
    List<Pedido> listarTodos();
    Optional<Pedido> buscarPorId(Integer codigoPedido);
    List<Pedido> listarUltimos(int limite);
    boolean actualizar(Pedido pedido);
    boolean eliminarPorId(Integer codigoPedido);
}
