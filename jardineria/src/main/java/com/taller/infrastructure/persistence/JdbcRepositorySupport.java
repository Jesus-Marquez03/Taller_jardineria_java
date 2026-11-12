package com.taller.infrastructure.persistence;

import com.taller.domain.exception.PersistenciaException;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

abstract class JdbcRepositorySupport {
    protected final DatabaseConfig databaseConfig;

    protected JdbcRepositorySupport(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    protected PersistenciaException error(String accion, SQLException exception) {
        String detalle = switch (exception.getSQLState() == null ? "" : exception.getSQLState().substring(0, Math.min(2, exception.getSQLState().length()))) {
            case "23" -> " Revisa identificadores duplicados y relaciones con otros registros.";
            case "22" -> " Revisa el formato y el tamano de los datos.";
            default -> "";
        };
        return new PersistenciaException("No se pudo " + accion + "." + detalle, exception);
    }

    protected void setIntegerNullable(PreparedStatement statement, int indice, Integer valor) throws SQLException {
        if (valor == null) statement.setNull(indice, Types.INTEGER); else statement.setInt(indice, valor);
    }

    protected Integer getIntegerNullable(ResultSet resultSet, String columna) throws SQLException {
        int valor = resultSet.getInt(columna);
        return resultSet.wasNull() ? null : valor;
    }
}
