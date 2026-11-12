package com.taller.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Centraliza la configuracion sin mantener una conexion abierta. */
public final class DatabaseConfig {
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    private final Properties properties = new Properties();
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfig() {
        cargarPropiedadesLocales();
        String host = valor("DB_HOST", "db.host", "localhost");
        String port = valor("DB_PORT", "db.port", "3306");
        String database = valor("DB_NAME", "db.name", "jardineria");
        this.url = valor("DB_URL", "db.url",
                "jdbc:mysql://" + host + ":" + port + "/" + database
                        + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Bogota");
        this.user = valor("DB_USER", "db.user", "jardineria_app");
        this.password = valor("DB_PASSWORD", "db.password", "");
    }

    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException exception) {
            throw new SQLException(
                    "No fue posible conectar con MySQL. Verifica Docker y las variables DB_HOST, DB_PORT, DB_NAME y DB_USER.",
                    exception.getSQLState(), exception.getErrorCode(), exception);
        }
    }

    public boolean probarConexion() {
        try (Connection connection = getConnection()) {
            return connection.isValid(3);
        } catch (SQLException exception) {
            return false;
        }
    }

    public String getUrl() {
        return url;
    }

    private void cargarPropiedadesLocales() {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer application.properties.", exception);
        }
    }

    private String valor(String variableEntorno, String propiedad, String predeterminado) {
        String desdeEntorno = System.getenv(variableEntorno);
        if (desdeEntorno != null && !desdeEntorno.isBlank()) {
            return desdeEntorno.trim();
        }
        String desdeArchivo = properties.getProperty(propiedad);
        return desdeArchivo == null || desdeArchivo.isBlank() ? predeterminado : desdeArchivo.trim();
    }
}
