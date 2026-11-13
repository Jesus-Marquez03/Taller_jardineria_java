package com.taller.infrastructure.persistence;

import com.taller.domain.model.Oficina;
import com.taller.domain.ports.out.OficinaRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador JDBC que cumple el puerto {@link OficinaRepository}.
 *
 * <p>{@code implements} obliga a proporcionar todas las operaciones del
 * contrato. Cada método abre una {@link Connection}, prepara SQL con
 * {@link PreparedStatement} y usa try-with-resources para cerrar recursos. Los
 * SELECT recorren un {@link ResultSet}; INSERT, UPDATE y DELETE usan
 * {@code executeUpdate()}.</p>
 */
public class OficinaJdbcRepository extends JdbcRepositorySupport implements OficinaRepository {
    /** Recibe la configuración y la entrega a la clase de soporte. */
    public OficinaJdbcRepository(DatabaseConfig config) { super(config); }

    /** Asigna las ocho columnas a parámetros y ejecuta un INSERT. */
    @Override public void guardar(Oficina o) {
        String sql = "INSERT INTO oficina VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            asignar(p, o); p.executeUpdate();
        } catch (SQLException e) { throw error("guardar la oficina", e); }
    }

    /** Ejecuta SELECT y convierte cada fila en un objeto {@link Oficina}. */
    @Override public List<Oficina> listarTodos() {
        String sql = "SELECT * FROM oficina ORDER BY codigo_oficina";
        List<Oficina> datos = new ArrayList<>();
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) datos.add(mapear(r));
            return datos;
        } catch (SQLException e) { throw error("listar las oficinas", e); }
    }

    /** Usa el marcador {@code ?} para buscar la clave sin concatenar al SQL. */
    @Override public Optional<Oficina> buscarPorId(String id) {
        String sql = "SELECT * FROM oficina WHERE codigo_oficina = ?";
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? Optional.of(mapear(r)) : Optional.empty(); }
        } catch (SQLException e) { throw error("buscar la oficina", e); }
    }

    /** @return si {@code executeUpdate()} modificó al menos una fila */
    @Override public boolean actualizar(Oficina o) {
        String sql = "UPDATE oficina SET ciudad=?, pais=?, region=?, codigo_postal=?, telefono=?, linea_direccion1=?, linea_direccion2=? WHERE codigo_oficina=?";
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, o.getCiudad()); p.setString(2, o.getPais()); p.setString(3, o.getRegion());
            p.setString(4, o.getCodigoPostal()); p.setString(5, o.getTelefono()); p.setString(6, o.getLineaDireccion1());
            p.setString(7, o.getLineaDireccion2()); p.setString(8, o.getCodigoOficina());
            return p.executeUpdate() > 0;
        } catch (SQLException e) { throw error("actualizar la oficina", e); }
    }

    /** Elimina por clave primaria; una clave foránea puede impedirlo en MySQL. */
    @Override public boolean eliminarPorId(String id) {
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM oficina WHERE codigo_oficina=?")) {
            p.setString(1, id); return p.executeUpdate() > 0;
        } catch (SQLException e) { throw error("eliminar la oficina", e); }
    }

    /** Coloca los atributos del objeto en el mismo orden de los signos {@code ?}. */
    private void asignar(PreparedStatement p, Oficina o) throws SQLException {
        p.setString(1, o.getCodigoOficina()); p.setString(2, o.getCiudad()); p.setString(3, o.getPais());
        p.setString(4, o.getRegion()); p.setString(5, o.getCodigoPostal()); p.setString(6, o.getTelefono());
        p.setString(7, o.getLineaDireccion1()); p.setString(8, o.getLineaDireccion2());
    }

    /** Transforma la fila actual del ResultSet en una entidad de dominio. */
    private Oficina mapear(ResultSet r) throws SQLException {
        return new Oficina(r.getString("codigo_oficina"), r.getString("ciudad"), r.getString("pais"),
                r.getString("region"), r.getString("codigo_postal"), r.getString("telefono"),
                r.getString("linea_direccion1"), r.getString("linea_direccion2"));
    }
}
