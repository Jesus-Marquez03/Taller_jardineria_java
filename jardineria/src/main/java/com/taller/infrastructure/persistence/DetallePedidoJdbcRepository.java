package com.taller.infrastructure.persistence;

import com.taller.domain.model.DetallePedido;
import com.taller.domain.model.DetallePedidoId;
import com.taller.domain.ports.out.DetallePedidoRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetallePedidoJdbcRepository extends JdbcRepositorySupport implements DetallePedidoRepository {
    public DetallePedidoJdbcRepository(DatabaseConfig config){super(config);}
    @Override public void guardar(DetallePedido x){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO detalle_pedido VALUES (?,?,?,?,?)")){asignar(p,x);p.executeUpdate();}catch(SQLException e){throw error("guardar el detalle del pedido",e);}}
    @Override public List<DetallePedido> listarTodos(){List<DetallePedido>d=new ArrayList<>();try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM detalle_pedido ORDER BY codigo_pedido,numero_linea");ResultSet r=p.executeQuery()){while(r.next())d.add(mapear(r));return d;}catch(SQLException e){throw error("listar los detalles de pedido",e);}}
    @Override public Optional<DetallePedido> buscarPorId(DetallePedidoId id){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM detalle_pedido WHERE codigo_pedido=? AND codigo_producto=?")){p.setInt(1,id.codigoPedido());p.setString(2,id.codigoProducto());try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}}catch(SQLException e){throw error("buscar el detalle del pedido",e);}}
    @Override public boolean actualizar(DetallePedido x){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("UPDATE detalle_pedido SET cantidad=?,precio_unidad=?,numero_linea=? WHERE codigo_pedido=? AND codigo_producto=?")){p.setInt(1,x.getCantidad());p.setBigDecimal(2,x.getPrecioUnidad());p.setInt(3,x.getNumeroLinea());p.setInt(4,x.getCodigoPedido());p.setString(5,x.getCodigoProducto());return p.executeUpdate()>0;}catch(SQLException e){throw error("actualizar el detalle del pedido",e);}}
    @Override public boolean eliminarPorId(DetallePedidoId id){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM detalle_pedido WHERE codigo_pedido=? AND codigo_producto=?")){p.setInt(1,id.codigoPedido());p.setString(2,id.codigoProducto());return p.executeUpdate()>0;}catch(SQLException e){throw error("eliminar el detalle del pedido",e);}}
    private void asignar(PreparedStatement p,DetallePedido x)throws SQLException{p.setInt(1,x.getCodigoPedido());p.setString(2,x.getCodigoProducto());p.setInt(3,x.getCantidad());p.setBigDecimal(4,x.getPrecioUnidad());p.setInt(5,x.getNumeroLinea());}
    private DetallePedido mapear(ResultSet r)throws SQLException{return new DetallePedido(r.getInt("codigo_pedido"),r.getString("codigo_producto"),r.getInt("cantidad"),r.getBigDecimal("precio_unidad"),r.getInt("numero_linea"));}
}
