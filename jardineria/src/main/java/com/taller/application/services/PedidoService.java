package com.taller.application.services;

import com.taller.application.exception.ReglaNegocioException;
import com.taller.application.ports.in.CrudUseCase;
import com.taller.domain.model.Pedido;
import com.taller.domain.ports.out.ClienteRepository;
import com.taller.domain.ports.out.PedidoRepository;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso de pedidos: valida cliente y coherencia de fechas.
 * La entidad comprueba datos básicos; este servicio aplica reglas entre campos
 * y relaciones con otros registros.
 */
public class PedidoService implements CrudUseCase<Pedido,Integer>{
    private final PedidoRepository repository;private final ClienteRepository clientes;
    /** Recibe los puertos de pedidos y clientes mediante el constructor. */
    public PedidoService(PedidoRepository repository,ClienteRepository clientes){this.repository=repository;this.clientes=clientes;}
    /** Comprueba duplicado, cliente y fechas antes de insertar. */
    @Override public void guardar(Pedido x){if(repository.buscarPorId(x.getCodigoPedido()).isPresent())throw new ReglaNegocioException("Ya existe ese pedido.");validar(x);repository.guardar(x);}
    @Override public List<Pedido> listarTodos(){return repository.listarTodos();}
    /** @return como máximo la cantidad solicitada de pedidos recientes */
    public List<Pedido> listarUltimos(int limite){return repository.listarUltimos(limite);}
    @Override public Optional<Pedido> buscarPorId(Integer id){return repository.buscarPorId(id);}
    @Override public void actualizar(Pedido x){exigir(x.getCodigoPedido());validar(x);if(!repository.actualizar(x))throw new ReglaNegocioException("El pedido no fue actualizado.");}
    @Override public void eliminar(Integer id){exigir(id);if(!repository.eliminarPorId(id))throw new ReglaNegocioException("El pedido no fue eliminado.");}
    /** Valida clave foránea y evita fechas esperadas o entregas anteriores al pedido. */
    private void validar(Pedido x){if(clientes.buscarPorId(x.getCodigoCliente()).isEmpty())throw new ReglaNegocioException("El cliente seleccionado no existe.");if(x.getFechaEsperada().isBefore(x.getFechaPedido()))throw new ReglaNegocioException("La fecha esperada no puede ser anterior a la fecha del pedido.");if(x.getFechaEntrega()!=null&&x.getFechaEntrega().isBefore(x.getFechaPedido()))throw new ReglaNegocioException("La fecha de entrega no puede ser anterior a la fecha del pedido.");}
    /** Exige que el pedido exista para modificarlo o borrarlo. */
    private void exigir(Integer id){if(repository.buscarPorId(id).isEmpty())throw new ReglaNegocioException("El pedido no existe.");}
}
