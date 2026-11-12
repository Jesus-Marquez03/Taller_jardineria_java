package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.application.ports.in.CrudUseCase;
import com.taller.domain.model.DetallePedido;
import com.taller.domain.model.DetallePedidoId;
import com.taller.domain.model.Producto;
import com.taller.domain.ports.out.DetallePedidoRepository;
import com.taller.domain.ports.out.PedidoRepository;
import com.taller.domain.ports.out.ProductoRepository;
import java.util.List;
import java.util.Optional;

public class DetallePedidoService implements CrudUseCase<DetallePedido,DetallePedidoId>{
    private final DetallePedidoRepository repository;private final PedidoRepository pedidos;private final ProductoRepository productos;
    public DetallePedidoService(DetallePedidoRepository repository,PedidoRepository pedidos,ProductoRepository productos){this.repository=repository;this.pedidos=pedidos;this.productos=productos;}
    @Override public void guardar(DetallePedido x){if(repository.buscarPorId(x.getId()).isPresent())throw new ReglaNegocioException("Ese producto ya esta incluido en el pedido.");validar(x);repository.guardar(x);}
    @Override public List<DetallePedido> listarTodos(){return repository.listarTodos();}
    @Override public Optional<DetallePedido> buscarPorId(DetallePedidoId id){return repository.buscarPorId(id);}
    @Override public void actualizar(DetallePedido x){exigir(x.getId());validar(x);if(!repository.actualizar(x))throw new ReglaNegocioException("El detalle no fue actualizado.");}
    @Override public void eliminar(DetallePedidoId id){exigir(id);if(!repository.eliminarPorId(id))throw new ReglaNegocioException("El detalle no fue eliminado.");}
    private void validar(DetallePedido x){if(pedidos.buscarPorId(x.getCodigoPedido()).isEmpty())throw new ReglaNegocioException("El pedido seleccionado no existe.");Producto producto=productos.buscarPorId(x.getCodigoProducto()).orElseThrow(()->new ReglaNegocioException("El producto seleccionado no existe."));if(x.getCantidad()>producto.getCantidadEnStock())throw new ReglaNegocioException("La cantidad supera el stock disponible ("+producto.getCantidadEnStock()+").");}
    private void exigir(DetallePedidoId id){if(repository.buscarPorId(id).isEmpty())throw new ReglaNegocioException("El detalle del pedido no existe.");}
}
