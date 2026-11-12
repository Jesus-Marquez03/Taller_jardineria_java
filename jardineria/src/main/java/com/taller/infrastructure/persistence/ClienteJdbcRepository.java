package com.taller.infrastructure.persistence;

import com.taller.domain.model.Cliente;
import com.taller.domain.ports.out.ClienteRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteJdbcRepository extends JdbcRepositorySupport implements ClienteRepository {
    public ClienteJdbcRepository(DatabaseConfig config){super(config);}
    @Override public void guardar(Cliente x){
        String sql="INSERT INTO cliente VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql)){asignar(p,x);p.executeUpdate();}
        catch(SQLException e){throw error("guardar el cliente",e);}
    }
    @Override public List<Cliente> listarTodos(){
        List<Cliente>d=new ArrayList<>();try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM cliente ORDER BY codigo_cliente");ResultSet r=p.executeQuery()){while(r.next())d.add(mapear(r));return d;}catch(SQLException e){throw error("listar los clientes",e);}
    }
    @Override public Optional<Cliente> buscarPorId(Integer id){
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM cliente WHERE codigo_cliente=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}}catch(SQLException e){throw error("buscar el cliente",e);}
    }
    @Override public boolean actualizar(Cliente x){
        String sql="UPDATE cliente SET nombre_cliente=?,nombre_contacto=?,apellido_contacto=?,telefono=?,fax=?,linea_direccion1=?,linea_direccion2=?,ciudad=?,region=?,pais=?,codigo_postal=?,codigo_empleado_rep_ventas=?,limite_credito=? WHERE codigo_cliente=?";
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,x.getNombreCliente());p.setString(2,x.getNombreContacto());p.setString(3,x.getApellidoContacto());p.setString(4,x.getTelefono());p.setString(5,x.getFax());p.setString(6,x.getLineaDireccion1());p.setString(7,x.getLineaDireccion2());p.setString(8,x.getCiudad());p.setString(9,x.getRegion());p.setString(10,x.getPais());p.setString(11,x.getCodigoPostal());setIntegerNullable(p,12,x.getCodigoEmpleadoRepVentas());p.setBigDecimal(13,x.getLimiteCredito());p.setInt(14,x.getCodigoCliente());return p.executeUpdate()>0;
        }catch(SQLException e){throw error("actualizar el cliente",e);}
    }
    @Override public boolean eliminarPorId(Integer id){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM cliente WHERE codigo_cliente=?")){p.setInt(1,id);return p.executeUpdate()>0;}catch(SQLException e){throw error("eliminar el cliente",e);}}
    private void asignar(PreparedStatement p,Cliente x)throws SQLException{p.setInt(1,x.getCodigoCliente());p.setString(2,x.getNombreCliente());p.setString(3,x.getNombreContacto());p.setString(4,x.getApellidoContacto());p.setString(5,x.getTelefono());p.setString(6,x.getFax());p.setString(7,x.getLineaDireccion1());p.setString(8,x.getLineaDireccion2());p.setString(9,x.getCiudad());p.setString(10,x.getRegion());p.setString(11,x.getPais());p.setString(12,x.getCodigoPostal());setIntegerNullable(p,13,x.getCodigoEmpleadoRepVentas());p.setBigDecimal(14,x.getLimiteCredito());}
    private Cliente mapear(ResultSet r)throws SQLException{return new Cliente(r.getInt("codigo_cliente"),r.getString("nombre_cliente"),r.getString("nombre_contacto"),r.getString("apellido_contacto"),r.getString("telefono"),r.getString("fax"),r.getString("linea_direccion1"),r.getString("linea_direccion2"),r.getString("ciudad"),r.getString("region"),r.getString("pais"),r.getString("codigo_postal"),getIntegerNullable(r,"codigo_empleado_rep_ventas"),r.getBigDecimal("limite_credito"));}
}
