package com.taller.infrastructure.persistence;

import com.taller.domain.model.DashboardData;
import com.taller.domain.model.Pedido;
import com.taller.domain.ports.out.DashboardRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardJdbcRepository extends JdbcRepositorySupport implements DashboardRepository {
    public DashboardJdbcRepository(DatabaseConfig config){super(config);}

    @Override public DashboardData obtenerResumen(int limiteStock,int limitePedidos){
        try(Connection c=databaseConfig.getConnection()){
            BigDecimal ingresos=BigDecimal.ZERO;int pagos=0;
            try(PreparedStatement p=c.prepareStatement("SELECT COALESCE(SUM(total),0) ingresos,COUNT(*) cantidad FROM pago");ResultSet r=p.executeQuery()){if(r.next()){ingresos=r.getBigDecimal("ingresos");pagos=r.getInt("cantidad");}}
            int pendientes=contar(c,"SELECT COUNT(*) FROM pedido WHERE estado=?","Pendiente");
            int entregados=contar(c,"SELECT COUNT(*) FROM pedido WHERE estado=?","Entregado");
            int clientes=contar(c,"SELECT COUNT(*) FROM cliente",null);
            int productos=contar(c,"SELECT COUNT(*) FROM producto",null);
            int criticos=contar(c,"SELECT COUNT(*) FROM producto WHERE cantidad_en_stock<=?",limiteStock);
            return new DashboardData(ingresos,pagos,pendientes,entregados,clientes,criticos,productos,ultimos(c,limitePedidos),estados(c));
        }catch(SQLException e){throw error("cargar el dashboard",e);}
    }
    private int contar(Connection c,String sql,Object parametro)throws SQLException{try(PreparedStatement p=c.prepareStatement(sql)){if(parametro instanceof String s)p.setString(1,s);else if(parametro instanceof Integer i)p.setInt(1,i);try(ResultSet r=p.executeQuery()){return r.next()?r.getInt(1):0;}}}
    private List<Pedido> ultimos(Connection c,int limite)throws SQLException{List<Pedido>d=new ArrayList<>();try(PreparedStatement p=c.prepareStatement("SELECT * FROM pedido ORDER BY fecha_pedido DESC,codigo_pedido DESC LIMIT ?")){p.setInt(1,limite);try(ResultSet r=p.executeQuery()){while(r.next())d.add(new Pedido(r.getInt("codigo_pedido"),r.getObject("fecha_pedido",LocalDate.class),r.getObject("fecha_esperada",LocalDate.class),r.getObject("fecha_entrega",LocalDate.class),r.getString("estado"),r.getString("comentarios"),r.getInt("codigo_cliente")));}}return d;}
    private Map<String,Integer> estados(Connection c)throws SQLException{Map<String,Integer>d=new LinkedHashMap<>();try(PreparedStatement p=c.prepareStatement("SELECT estado,COUNT(*) cantidad FROM pedido GROUP BY estado ORDER BY estado");ResultSet r=p.executeQuery()){while(r.next())d.put(r.getString("estado"),r.getInt("cantidad"));}return d;}
}
