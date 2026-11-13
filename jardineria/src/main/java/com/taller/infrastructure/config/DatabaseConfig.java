package com.taller.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centraliza los datos necesarios para conectarse con MySQL.
 *
 * <p>Es infraestructura porque utiliza JDBC y detalles externos. Aplica
 * Singleton: existe una sola configuración accesible con {@link #getInstance()},
 * pero no conserva una única conexión abierta. Cada repositorio solicita una
 * {@link Connection} nueva y la cierra mediante try-with-resources.</p>
 */
public final class DatabaseConfig {
    /** Instancia única creada por la propia clase. */
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    private final Properties properties = new Properties();
    private final String url;
    private final String user;
    private final String password;

    /**
     * Constructor privado del Singleton. Lee primero variables de entorno,
     * después propiedades locales y finalmente valores de desarrollo.
     */
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

    /** @return la única configuración compartida por la aplicación */
    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Abre una conexión JDBC nueva mediante {@link DriverManager}.
     * @return conexión que el llamador debe cerrar
     * @throws SQLException si MySQL no está disponible o la configuración falla
     */
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException exception) {
            throw new SQLException(
                    "No fue posible conectar con MySQL. Verifica Docker y las variables DB_HOST, DB_PORT, DB_NAME y DB_USER.",
                    exception.getSQLState(), exception.getErrorCode(), exception);
        }
    }

    /**
     * Abre y cierra una conexión corta para comprobar disponibilidad.
     * @return {@code true} cuando MySQL responde dentro de tres segundos
     */
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

    /** Carga application.properties cuando existe; el archivo real no se versiona. */
    private void cargarPropiedadesLocales() {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer application.properties.", exception);
        }
    }

    /** Resuelve un valor respetando el orden entorno, archivo y predeterminado. */
    private String valor(String variableEntorno, String propiedad, String predeterminado) {
        String desdeEntorno = System.getenv(variableEntorno);
        if (desdeEntorno != null && !desdeEntorno.isBlank()) {
            return desdeEntorno.trim();
        }
        String desdeArchivo = properties.getProperty(propiedad);
        return desdeArchivo == null || desdeArchivo.isBlank() ? predeterminado : desdeArchivo.trim();
    }
}
