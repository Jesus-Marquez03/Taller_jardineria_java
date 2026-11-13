package com.taller.infrastructure.persistence;

import com.taller.domain.model.Empleado;
import com.taller.domain.ports.out.EmpleadoRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de infraestructura que implementa {@link EmpleadoRepository} con JDBC.
 * Usa sentencias preparadas, cierra recursos automáticamente y mapea filas a
 * objetos {@link Empleado}. También conserva como {@code null} el jefe opcional.
 */
public class EmpleadoJdbcRepository extends JdbcRepositorySupport implements EmpleadoRepository {
    /** Guarda la configuración; todavía no abre una conexión. */
    public EmpleadoJdbcRepository(DatabaseConfig config) { super(config); }

    /** Inserta las nueve columnas después de asignar sus parámetros. */
    @Override public void guardar(Empleado e) {
        String sql = "INSERT INTO empleado VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            asignar(p, e); p.executeUpdate();
        } catch (SQLException x) { throw error("guardar el empleado", x); }
    }

    /** Recorre el ResultSet de un SELECT y construye la lista. */
    @Override public List<Empleado> listarTodos() {
        List<Empleado> datos = new ArrayList<>();
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM empleado ORDER BY codigo_empleado"); ResultSet r = p.executeQuery()) {
            while (r.next()) datos.add(mapear(r)); return datos;
        } catch (SQLException x) { throw error("listar los empleados", x); }
    }

    /** @return un Optional vacío cuando {@code ResultSet.next()} es falso */
    @Override public Optional<Empleado> buscarPorId(Integer id) {
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM empleado WHERE codigo_empleado=?")) {
            p.setInt(1, id); try (ResultSet r = p.executeQuery()) { return r.next() ? Optional.of(mapear(r)) : Optional.empty(); }
        } catch (SQLException x) { throw error("buscar el empleado", x); }
    }

    /** Actualiza por clave primaria y maneja correctamente un jefe SQL NULL. */
    @Override public boolean actualizar(Empleado e) {
        String sql = "UPDATE empleado SET nombre=?, apellido1=?, apellido2=?, extension=?, email=?, codigo_oficina=?, codigo_jefe=?, puesto=? WHERE codigo_empleado=?";
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, e.getNombre()); p.setString(2, e.getApellido1()); p.setString(3, e.getApellido2());
            p.setString(4, e.getExtension()); p.setString(5, e.getEmail()); p.setString(6, e.getCodigoOficina());
            setIntegerNullable(p, 7, e.getCodigoJefe()); p.setString(8, e.getPuesto()); p.setInt(9, e.getCodigoEmpleado());
            return p.executeUpdate() > 0;
        } catch (SQLException x) { throw error("actualizar el empleado", x); }
    }

    /** Elimina el empleado si las relaciones de MySQL lo permiten. */
    @Override public boolean eliminarPorId(Integer id) {
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM empleado WHERE codigo_empleado=?")) {
            p.setInt(1, id); return p.executeUpdate() > 0;
        } catch (SQLException x) { throw error("eliminar el empleado", x); }
    }

    /** Asigna atributos Java a los parámetros del INSERT. */
    private void asignar(PreparedStatement p, Empleado e) throws SQLException {
        p.setInt(1, e.getCodigoEmpleado()); p.setString(2, e.getNombre()); p.setString(3, e.getApellido1());
        p.setString(4, e.getApellido2()); p.setString(5, e.getExtension()); p.setString(6, e.getEmail());
        p.setString(7, e.getCodigoOficina()); setIntegerNullable(p, 8, e.getCodigoJefe()); p.setString(9, e.getPuesto());
    }

    /** Convierte columnas de la fila actual en una entidad. */
    private Empleado mapear(ResultSet r) throws SQLException {
        return new Empleado(r.getInt("codigo_empleado"), r.getString("nombre"), r.getString("apellido1"),
                r.getString("apellido2"), r.getString("extension"), r.getString("email"),
                r.getString("codigo_oficina"), getIntegerNullable(r, "codigo_jefe"), r.getString("puesto"));
    }
}
