package com.taller.infrastructure.persistence;

import com.taller.domain.exception.PersistenciaException;
import com.taller.infrastructure.config.DatabaseConfig;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Base pequeña con utilidades comunes para los adaptadores JDBC.
 *
 * <p>No es un repositorio por sí sola. Guarda la configuración, traduce
 * {@link SQLException} a una excepción entendible y maneja enteros que pueden
 * ser NULL. La herencia se usa aquí solo para compartir estas operaciones
 * sencillas entre los ocho adaptadores.</p>
 */
abstract class JdbcRepositorySupport {
    protected final DatabaseConfig databaseConfig;

    /** Recibe la configuración sin abrir todavía una conexión. */
    protected JdbcRepositorySupport(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Convierte códigos SQL en un mensaje útil y conserva la causa original.
     * El estado 23 suele indicar claves duplicadas o foráneas; el 22, datos inválidos.
     */
    protected PersistenciaException error(String accion, SQLException exception) {
        String detalle = switch (exception.getSQLState() == null ? "" : exception.getSQLState().substring(0, Math.min(2, exception.getSQLState().length()))) {
            case "23" -> " Revisa identificadores duplicados y relaciones con otros registros.";
            case "22" -> " Revisa el formato y el tamano de los datos.";
            default -> "";
        };
        return new PersistenciaException("No se pudo " + accion + "." + detalle, exception);
    }

    /** Asigna un entero o SQL NULL a un parámetro de {@link PreparedStatement}. */
    protected void setIntegerNullable(PreparedStatement statement, int indice, Integer valor) throws SQLException {
        if (valor == null) statement.setNull(indice, Types.INTEGER); else statement.setInt(indice, valor);
    }

    /** Lee un entero y usa {@code wasNull()} para conservar SQL NULL como Java null. */
    protected Integer getIntegerNullable(ResultSet resultSet, String columna) throws SQLException {
        int valor = resultSet.getInt(columna);
        return resultSet.wasNull() ? null : valor;
    }
}
