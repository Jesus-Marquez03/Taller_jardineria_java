package com.taller.infrastructure.persistence;

import com.taller.domain.model.GamaProducto;
import com.taller.domain.ports.out.GamaProductoRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GamaProductoJdbcRepository extends JdbcRepositorySupport implements GamaProductoRepository {
    public GamaProductoJdbcRepository(DatabaseConfig config) { super(config); }

    @Override public void guardar(GamaProducto g) {
        try (Connection c=databaseConfig.getConnection(); PreparedStatement p=c.prepareStatement("INSERT INTO gama_producto VALUES (?, ?, ?, ?)")) {
            asignar(p,g); p.executeUpdate();
        } catch(SQLException e){ throw error("guardar la gama",e); }
    }
    @Override public List<GamaProducto> listarTodos(){
        List<GamaProducto> datos=new ArrayList<>();
        try(Connection c=databaseConfig.getConnection(); PreparedStatement p=c.prepareStatement("SELECT * FROM gama_producto ORDER BY gama"); ResultSet r=p.executeQuery()){
            while(r.next()) datos.add(mapear(r)); return datos;
        }catch(SQLException e){throw error("listar las gamas",e);}
    }
    @Override public Optional<GamaProducto> buscarPorId(String id){
        try(Connection c=databaseConfig.getConnection(); PreparedStatement p=c.prepareStatement("SELECT * FROM gama_producto WHERE gama=?")){
            p.setString(1,id); try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(mapear(r)):Optional.empty();}
        }catch(SQLException e){throw error("buscar la gama",e);}
    }
    @Override public boolean actualizar(GamaProducto g){
        try(Connection c=databaseConfig.getConnection(); PreparedStatement p=c.prepareStatement("UPDATE gama_producto SET descripcion_texto=?, descripcion_html=?, imagen=? WHERE gama=?")){
            p.setString(1,g.getDescripcionTexto());p.setString(2,g.getDescripcionHtml());p.setString(3,g.getImagen());p.setString(4,g.getGama());return p.executeUpdate()>0;
        }catch(SQLException e){throw error("actualizar la gama",e);}
    }
    @Override public boolean eliminarPorId(String id){
        try(Connection c=databaseConfig.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM gama_producto WHERE gama=?")){p.setString(1,id);return p.executeUpdate()>0;}
        catch(SQLException e){throw error("eliminar la gama",e);}
    }
    private void asignar(PreparedStatement p,GamaProducto g)throws SQLException{p.setString(1,g.getGama());p.setString(2,g.getDescripcionTexto());p.setString(3,g.getDescripcionHtml());p.setString(4,g.getImagen());}
    private GamaProducto mapear(ResultSet r)throws SQLException{return new GamaProducto(r.getString("gama"),r.getString("descripcion_texto"),r.getString("descripcion_html"),r.getString("imagen"));}
}
