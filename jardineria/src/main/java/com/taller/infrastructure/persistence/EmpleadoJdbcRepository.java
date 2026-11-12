package com.taller.infrastructure.persistence;

import com.taller.domain.model.Empleado;
import com.taller.domain.ports.out.EmpleadoRepository;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpleadoJdbcRepository extends JdbcRepositorySupport implements EmpleadoRepository {
    public EmpleadoJdbcRepository(DatabaseConfig config) { super(config); }

    @Override public void guardar(Empleado e) {
        String sql = "INSERT INTO empleado VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            asignar(p, e); p.executeUpdate();
        } catch (SQLException x) { throw error("guardar el empleado", x); }
    }

    @Override public List<Empleado> listarTodos() {
        List<Empleado> datos = new ArrayList<>();
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM empleado ORDER BY codigo_empleado"); ResultSet r = p.executeQuery()) {
            while (r.next()) datos.add(mapear(r)); return datos;
        } catch (SQLException x) { throw error("listar los empleados", x); }
    }

    @Override public Optional<Empleado> buscarPorId(Integer id) {
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM empleado WHERE codigo_empleado=?")) {
            p.setInt(1, id); try (ResultSet r = p.executeQuery()) { return r.next() ? Optional.of(mapear(r)) : Optional.empty(); }
        } catch (SQLException x) { throw error("buscar el empleado", x); }
    }

    @Override public boolean actualizar(Empleado e) {
        String sql = "UPDATE empleado SET nombre=?, apellido1=?, apellido2=?, extension=?, email=?, codigo_oficina=?, codigo_jefe=?, puesto=? WHERE codigo_empleado=?";
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, e.getNombre()); p.setString(2, e.getApellido1()); p.setString(3, e.getApellido2());
            p.setString(4, e.getExtension()); p.setString(5, e.getEmail()); p.setString(6, e.getCodigoOficina());
            setIntegerNullable(p, 7, e.getCodigoJefe()); p.setString(8, e.getPuesto()); p.setInt(9, e.getCodigoEmpleado());
            return p.executeUpdate() > 0;
        } catch (SQLException x) { throw error("actualizar el empleado", x); }
    }

    @Override public boolean eliminarPorId(Integer id) {
        try (Connection c = databaseConfig.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM empleado WHERE codigo_empleado=?")) {
            p.setInt(1, id); return p.executeUpdate() > 0;
        } catch (SQLException x) { throw error("eliminar el empleado", x); }
    }

    private void asignar(PreparedStatement p, Empleado e) throws SQLException {
        p.setInt(1, e.getCodigoEmpleado()); p.setString(2, e.getNombre()); p.setString(3, e.getApellido1());
        p.setString(4, e.getApellido2()); p.setString(5, e.getExtension()); p.setString(6, e.getEmail());
        p.setString(7, e.getCodigoOficina()); setIntegerNullable(p, 8, e.getCodigoJefe()); p.setString(9, e.getPuesto());
    }

    private Empleado mapear(ResultSet r) throws SQLException {
        return new Empleado(r.getInt("codigo_empleado"), r.getString("nombre"), r.getString("apellido1"),
                r.getString("apellido2"), r.getString("extension"), r.getString("email"),
                r.getString("codigo_oficina"), getIntegerNullable(r, "codigo_jefe"), r.getString("puesto"));
    }
}
