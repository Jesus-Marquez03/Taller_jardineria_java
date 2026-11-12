package com.taller.domain.ports.out;

import com.taller.domain.model.DetallePedido;
import com.taller.domain.model.DetallePedidoId;
import java.util.List;
import java.util.Optional;

public interface DetallePedidoRepository {
    void guardar(DetallePedido detallePedido);
    List<DetallePedido> listarTodos();
    Optional<DetallePedido> buscarPorId(DetallePedidoId id);
    boolean actualizar(DetallePedido detallePedido);
    boolean eliminarPorId(DetallePedidoId id);
}
