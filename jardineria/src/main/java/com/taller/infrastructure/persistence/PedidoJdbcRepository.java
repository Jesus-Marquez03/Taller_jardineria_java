package com.taller.infrastructure.persistence;

import com.taller.domain.model.Pedido;
import com.taller.domain.ports.out.PedidoRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoJdbcRepository extends JdbcRepositorySupport implements PedidoRepository {
    public PedidoJdbcRepository(DatabaseConfig config){super(config);}
    @Override public void guardar(Pedido x){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO pedido VALUES (?,?,?,?,?,?,?)")){asignar(p,x);p.executeUpdate();}catch(SQLException e){throw error("guardar el pedido",e);}}
    @Override public List<Pedido> listarTodos(){return consultar("SELECT * FROM pedido ORDER BY codigo_pedido",null);}
    @Override public Optional<Pedido> buscarPorId(Integer id){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM pedido WHERE codigo_pedido=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}}catch(SQLException e){throw error("buscar el pedido",e);}}
    @Override public List<Pedido> listarUltimos(int limite){return consultar("SELECT * FROM pedido ORDER BY fecha_pedido DESC,codigo_pedido DESC LIMIT ?",limite);}
    @Override public boolean actualizar(Pedido x){
        String sql="UPDATE pedido SET fecha_pedido=?,fecha_esperada=?,fecha_entrega=?,estado=?,comentarios=?,codigo_cliente=? WHERE codigo_pedido=?";
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setObject(1,x.getFechaPedido());p.setObject(2,x.getFechaEsperada());p.setObject(3,x.getFechaEntrega());p.setString(4,x.getEstado());p.setString(5,x.getComentarios());p.setInt(6,x.getCodigoCliente());p.setInt(7,x.getCodigoPedido());return p.executeUpdate()>0;}catch(SQLException e){throw error("actualizar el pedido",e);}
    }
    @Override public boolean eliminarPorId(Integer id){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM pedido WHERE codigo_pedido=?")){p.setInt(1,id);return p.executeUpdate()>0;}catch(SQLException e){throw error("eliminar el pedido",e);}}
    private List<Pedido> consultar(String sql,Integer limite){List<Pedido>d=new ArrayList<>();try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql)){if(limite!=null)p.setInt(1,limite);try(ResultSet r=p.executeQuery()){while(r.next())d.add(mapear(r));}return d;}catch(SQLException e){throw error("listar los pedidos",e);}}
    private void asignar(PreparedStatement p,Pedido x)throws SQLException{p.setInt(1,x.getCodigoPedido());p.setObject(2,x.getFechaPedido());p.setObject(3,x.getFechaEsperada());p.setObject(4,x.getFechaEntrega());p.setString(5,x.getEstado());p.setString(6,x.getComentarios());p.setInt(7,x.getCodigoCliente());}
    private Pedido mapear(ResultSet r)throws SQLException{return new Pedido(r.getInt("codigo_pedido"),r.getObject("fecha_pedido",LocalDate.class),r.getObject("fecha_esperada",LocalDate.class),r.getObject("fecha_entrega",LocalDate.class),r.getString("estado"),r.getString("comentarios"),r.getInt("codigo_cliente"));}
}
