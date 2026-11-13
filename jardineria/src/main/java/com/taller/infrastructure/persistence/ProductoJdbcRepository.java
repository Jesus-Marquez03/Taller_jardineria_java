package com.taller.infrastructure.persistence;

import com.taller.domain.model.Producto;
import com.taller.domain.ports.out.ProductoRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador JDBC que implementa el puerto {@link ProductoRepository}.
 *
 * <p>Es un buen ejemplo del patrón Repository: oculta SELECT, INSERT, UPDATE y
 * DELETE detrás de métodos con nombres del negocio. PreparedStatement recibe
 * los valores con métodos tipados y ResultSet se transforma en {@link Producto}.</p>
 */
public class ProductoJdbcRepository extends JdbcRepositorySupport implements ProductoRepository {
    /** Guarda la configuración que abrirá conexiones por operación. */
    public ProductoJdbcRepository(DatabaseConfig config){super(config);}
    /** Inserta un producto; executeUpdate ejecuta SQL que cambia filas. */
    @Override public void guardar(Producto x){
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO producto VALUES (?,?,?,?,?,?,?,?,?)")){asignar(p,x);p.executeUpdate();}catch(SQLException e){throw error("guardar el producto",e);}
    }
    /** Reutiliza la consulta auxiliar sin parámetro de stock. */
    @Override public List<Producto> listarTodos(){return consultarLista("SELECT * FROM producto ORDER BY codigo_producto",null);}
    /** Ejecuta un SELECT por clave y lee como máximo una fila. */
    @Override public Optional<Producto> buscarPorId(String id){
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM producto WHERE codigo_producto=?")){p.setString(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}}catch(SQLException e){throw error("buscar el producto",e);}
    }
    /** Asigna el límite al marcador {@code ?} y devuelve el inventario crítico. */
    @Override public List<Producto> listarConStockMenorOIgual(int limite){return consultarLista("SELECT * FROM producto WHERE cantidad_en_stock<=? ORDER BY cantidad_en_stock",limite);}
    /** Actualiza campos editables y devuelve si MySQL encontró la clave. */
    @Override public boolean actualizar(Producto x){
        String sql="UPDATE producto SET nombre=?,gama=?,dimensiones=?,proveedor=?,descripcion=?,cantidad_en_stock=?,precio_venta=?,precio_proveedor=? WHERE codigo_producto=?";
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,x.getNombre());p.setString(2,x.getGama());p.setString(3,x.getDimensiones());p.setString(4,x.getProveedor());p.setString(5,x.getDescripcion());p.setInt(6,x.getCantidadEnStock());p.setBigDecimal(7,x.getPrecioVenta());p.setBigDecimal(8,x.getPrecioProveedor());p.setString(9,x.getCodigoProducto());return p.executeUpdate()>0;}catch(SQLException e){throw error("actualizar el producto",e);}
    }
    /** Elimina por clave primaria cuando no existen detalles dependientes. */
    @Override public boolean eliminarPorId(String id){try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM producto WHERE codigo_producto=?")){p.setString(1,id);return p.executeUpdate()>0;}catch(SQLException e){throw error("eliminar el producto",e);}}
    /**
     * Ejecuta un SELECT, recorre el ResultSet con {@code next()} y mapea cada fila.
     * El try-with-resources cierra ResultSet, PreparedStatement y Connection.
     */
    private List<Producto> consultarLista(String sql,Integer limite){List<Producto>d=new ArrayList<>();try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql)){if(limite!=null)p.setInt(1,limite);try(ResultSet r=p.executeQuery()){while(r.next())d.add(mapear(r));}return d;}catch(SQLException e){throw error("listar los productos",e);}}
    /** Asigna atributos a los nueve parámetros del INSERT. */
    private void asignar(PreparedStatement p,Producto x)throws SQLException{p.setString(1,x.getCodigoProducto());p.setString(2,x.getNombre());p.setString(3,x.getGama());p.setString(4,x.getDimensiones());p.setString(5,x.getProveedor());p.setString(6,x.getDescripcion());p.setInt(7,x.getCantidadEnStock());p.setBigDecimal(8,x.getPrecioVenta());p.setBigDecimal(9,x.getPrecioProveedor());}
    /** Convierte las columnas de una fila en un objeto de dominio. */
    private Producto mapear(ResultSet r)throws SQLException{return new Producto(r.getString("codigo_producto"),r.getString("nombre"),r.getString("gama"),r.getString("dimensiones"),r.getString("proveedor"),r.getString("descripcion"),r.getInt("cantidad_en_stock"),r.getBigDecimal("precio_venta"),r.getBigDecimal("precio_proveedor"));}
}
