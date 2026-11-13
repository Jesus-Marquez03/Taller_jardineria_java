package com.taller.infrastructure.persistence;

import com.taller.domain.model.GamaProducto;
import com.taller.domain.ports.out.GamaProductoRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador JDBC para la tabla {@code gama_producto}.
 * Implementa el puerto del dominio y traduce entre filas SQL y
 * {@link GamaProducto}, sin llevar reglas de negocio a la persistencia.
 */
public class GamaProductoJdbcRepository extends JdbcRepositorySupport implements GamaProductoRepository {
    /** Recibe la configuración de conexiones. */
    public GamaProductoJdbcRepository(DatabaseConfig config) { super(config); }

    /** Inserta una gama usando parámetros preparados. */
    @Override public void guardar(GamaProducto g) {
        try (Connection c=databaseConfig.getConnection(); PreparedStatement p=c.prepareStatement("INSERT INTO gama_producto VALUES (?, ?, ?, ?)")) {
            asignar(p,g); p.executeUpdate();
        } catch(SQLException e){ throw error("guardar la gama",e); }
    }
    /** Lee todas las filas y las mapea a objetos. */
    @Override public List<GamaProducto> listarTodos(){
        List<GamaProducto> datos=new ArrayList<>();
        try(Connection c=databaseConfig.getConnection(); PreparedStatement p=c.prepareStatement("SELECT * FROM gama_producto ORDER BY gama"); ResultSet r=p.executeQuery()){
            while(r.next()) datos.add(mapear(r)); return datos;
        }catch(SQLException e){throw error("listar las gamas",e);}
    }
    /** Busca la clave textual de la gama. */
    @Override public Optional<GamaProducto> buscarPorId(String id){
        try(Connection c=databaseConfig.getConnection(); PreparedStatement p=c.prepareStatement("SELECT * FROM gama_producto WHERE gama=?")){
            p.setString(1,id); try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}
        }catch(SQLException e){throw error("buscar la gama",e);}
    }
    /** Modifica descripciones e imagen, pero no la clave primaria. */
    @Override public boolean actualizar(GamaProducto g){
        try(Connection c=databaseConfig.getConnection(); PreparedStatement p=c.prepareStatement("UPDATE gama_producto SET descripcion_texto=?, descripcion_html=?, imagen=? WHERE gama=?")){
            p.setString(1,g.getDescripcionTexto());p.setString(2,g.getDescripcionHtml());p.setString(3,g.getImagen());p.setString(4,g.getGama());return p.executeUpdate()>0;
        }catch(SQLException e){throw error("actualizar la gama",e);}
    }
    /** Elimina solo cuando no existen productos dependientes. */
    @Override public boolean eliminarPorId(String id){
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM gama_producto WHERE gama=?")){p.setString(1,id);return p.executeUpdate()>0;}
        catch(SQLException e){throw error("eliminar la gama",e);}
    }
    /** Asigna las columnas del INSERT en orden. */
    private void asignar(PreparedStatement p,GamaProducto g)throws SQLException{p.setString(1,g.getGama());p.setString(2,g.getDescripcionTexto());p.setString(3,g.getDescripcionHtml());p.setString(4,g.getImagen());}
    /** Construye la entidad desde el ResultSet actual. */
    private GamaProducto mapear(ResultSet r)throws SQLException{return new GamaProducto(r.getString("gama"),r.getString("descripcion_texto"),r.getString("descripcion_html"),r.getString("imagen"));}
}
