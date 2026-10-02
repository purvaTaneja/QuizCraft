package util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection configuration.
 *
 * Credentials are resolved in this order:
 *   1. Environment variables (DB_URL, DB_USER, DB_PASSWORD)
 *   2. Local properties file pointed to by -Dquizcraft.config=<path>
 *      or the default file ./db.local.properties
 *   3. Safe defaults
 *
 * The local properties file is intentionally NOT committed to version control
 * (see .gitignore) so real credentials never reach a public repository.
 */
public class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mariadb://localhost:3306/quizcraft?authenticationPlugins=MariaDB Native Password Authentication";
    private static final String DEFAULT_USER = "quizcraft_app";
    private static final String DEFAULT_PASSWORD = "";

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = loadLocalProperties();

        URL = resolve("DB_URL", props, "db.url", DEFAULT_URL);
        USER = resolve("DB_USER", props, "db.user", DEFAULT_USER);
        PASSWORD = resolve("DB_PASSWORD", props, "db.password", DEFAULT_PASSWORD);
    }

    private static String resolve(String envKey, Properties props, String propKey, String fallback) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        String propValue = props.getProperty(propKey);
        if (propValue != null && !propValue.isEmpty()) {
            return propValue;
        }
        return fallback;
    }

    private static Properties loadLocalProperties() {
        Properties props = new Properties();
        String path = System.getProperty("quizcraft.config");
        if (path == null || path.isEmpty()) {
            path = "db.local.properties";
        }
        try (InputStream in = new FileInputStream(path)) {
            props.load(in);
        } catch (Exception e) {
            // No local config file present. Fall back to defaults / env vars.
        }
        return props;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB JDBC Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
