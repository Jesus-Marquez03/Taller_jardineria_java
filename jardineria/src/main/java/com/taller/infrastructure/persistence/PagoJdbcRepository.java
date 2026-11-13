package com.taller.infrastructure.persistence;

import com.taller.domain.model.Pago;
import com.taller.domain.model.PagoId;
import com.taller.domain.ports.out.PagoRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador JDBC de pagos.
 * Usa las dos columnas de {@link PagoId} en cada WHERE y conserva dinero con
 * BigDecimal y fechas con {@link LocalDate}.
 */
public class PagoJdbcRepository extends JdbcRepositorySupport implements PagoRepository {
    /** Recibe la configuración compartida. */
    public PagoJdbcRepository(DatabaseConfig config){super(config);}
    /** Inserta un pago con executeUpdate. */
    @Override public void guardar(Pago x){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO pago VALUES (?,?,?,?,?)")){asignar(p,x);p.executeUpdate();}catch(SQLException e){throw error("guardar el pago",e);}}
    /** Consulta pagos y convierte el ResultSet en lista. */
    @Override public List<Pago> listarTodos(){List<Pago>d=new ArrayList<>();try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM pago ORDER BY fecha_pago DESC,codigo_cliente");ResultSet r=p.executeQuery()){while(r.next())d.add(mapear(r));return d;}catch(SQLException e){throw error("listar los pagos",e);}}
    /** Busca usando cliente y transacción como clave conjunta. */
    @Override public Optional<Pago> buscarPorId(PagoId id){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM pago WHERE codigo_cliente=? AND id_transaccion=?")){p.setInt(1,id.codigoCliente());p.setString(2,id.idTransaccion());try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}}catch(SQLException e){throw error("buscar el pago",e);}}
    /** Actualiza campos editables y mantiene la clave compuesta. */
    @Override public boolean actualizar(Pago x){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("UPDATE pago SET forma_pago=?,fecha_pago=?,total=? WHERE codigo_cliente=? AND id_transaccion=?")){p.setString(1,x.getFormaPago());p.setObject(2,x.getFechaPago());p.setBigDecimal(3,x.getTotal());p.setInt(4,x.getCodigoCliente());p.setString(5,x.getIdTransaccion());return p.executeUpdate()>0;}catch(SQLException e){throw error("actualizar el pago",e);}}
    /** Elimina la fila identificada por ambas partes de PagoId. */
    @Override public boolean eliminarPorId(PagoId id){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM pago WHERE codigo_cliente=? AND id_transaccion=?")){p.setInt(1,id.codigoCliente());p.setString(2,id.idTransaccion());return p.executeUpdate()>0;}catch(SQLException e){throw error("eliminar el pago",e);}}
    /** Asigna al INSERT cliente, forma, transacción, fecha y total. */
    private void asignar(PreparedStatement p,Pago x)throws SQLException{p.setInt(1,x.getCodigoCliente());p.setString(2,x.getFormaPago());p.setString(3,x.getIdTransaccion());p.setObject(4,x.getFechaPago());p.setBigDecimal(5,x.getTotal());}
    /** Construye el Pago desde las columnas de la fila actual. */
    private Pago mapear(ResultSet r)throws SQLException{return new Pago(r.getInt("codigo_cliente"),r.getString("forma_pago"),r.getString("id_transaccion"),r.getObject("fecha_pago",LocalDate.class),r.getBigDecimal("total"));}
}
