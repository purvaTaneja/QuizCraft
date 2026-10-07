package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
 *
 * <h2>Production (managed MySQL, e.g. Aiven MySQL 8.4)</h2>
 * Set DB_URL to a JDBC URL that selects the application database and requires TLS:
 * <pre>
 *   jdbc:mysql://HOST:PORT/quizcraft?sslMode=VERIFY_CA
 * </pre>
 * Notes:
 * <ul>
 *   <li>Production {@code jdbc:mysql:} URLs use MySQL Connector/J and its
 *       {@code VERIFY_CA} mode. Local {@code jdbc:mariadb:} URLs continue to
 *       use MariaDB Connector/J.</li>
 *   <li>When {@code DB_SSL_CA} is set, the CA is converted to a temporary
 *       PKCS#12 truststore for MySQL Connector/J. MariaDB Connector/J uses its
 *       documented {@code serverSslCert} option instead.</li>
 *   <li>Set DB_SSL_CA to a PEM certificate authority file to have the connection
 *       verify the server certificate. The PEM is converted to a temporary
 *       PKCS#12 truststore and sslMode is forced to VERIFY_CA, so a weakened
 *       sslMode in DB_URL cannot silently downgrade the check. Leave DB_SSL_CA
 *       unset to keep the URL exactly as configured.</li>
 * </ul>
 *
 * <h2>Local development</h2>
 * Local MariaDB settings live in the untracked db.local.properties file, which
 * takes precedence over these defaults.
 */
public class DBConnection {

    /**
     * Portable localhost default understood by both MariaDB and MySQL servers.
     * It deliberately omits any server-specific authentication plugin so it
     * cannot break against MySQL. Production always supplies DB_URL explicitly.
     */
    private static final String DEFAULT_URL = "jdbc:mariadb://localhost:3306/quizcraft";
    private static final String DEFAULT_USER = "quizcraft_app";
    private static final String DEFAULT_PASSWORD = "";

    /** Encrypts the connection and verifies the server certificate against the configured CA. */
    private static final String VERIFY_CA_SSL_MODE = "VERIFY_CA";

    /** Random per JVM run; guards only a locally generated truststore of public certificates. */
    private static final String TRUST_STORE_PASSWORD = generateTrustStorePassword();

    private static final String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static String generateTrustStorePassword() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = loadLocalProperties();

        URL = withCertificateVerification(
                resolve("DB_URL", props, "db.url", DEFAULT_URL),
                resolve("DB_SSL_CA", props, "db.ssl.ca", ""));
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

    /**
     * Upgrades a JDBC URL to verify the database server's TLS certificate.
     *
     * <p>The driver resolves {@code sslMode} from the URL and ignores an
     * {@code sslMode} supplied as a connection property, so the URL itself has to
     * be rewritten. The PEM certificate authority file is converted into a
     * throw-away PKCS#12 truststore in the JVM temp directory; its password is
     * random per process and never persisted, because a public CA certificate is
     * not a secret and needs no fixed password of its own.
     *
     * <p>If no CA file is configured the URL is returned untouched, so local
     * development and any plain-TLS deployment behave exactly as before.
     *
     * @param url    the configured JDBC URL
     * @param caFile path to a PEM certificate authority file, or blank to disable
     * @return the URL to connect with
     */
    private static String withCertificateVerification(String url, String caFile) {
        if (caFile == null || caFile.isEmpty()) {
            return url;
        }

        String upgraded = withSslMode(url, VERIFY_CA_SSL_MODE);
        if (url.startsWith("jdbc:mariadb://")) {
            return withQueryOptions(upgraded, List.<String[]>of(
                    new String[] {"serverSslCert", caFile}),
                    "trustStore", "trustStoreType", "trustStorePassword",
                    "trustCertificateKeyStoreUrl", "trustCertificateKeyStoreType",
                    "trustCertificateKeyStorePassword");
        }
        if (url.startsWith("jdbc:mysql://")) {
            File trustStore;
            try {
                trustStore = buildTrustStore(caFile);
            } catch (Exception e) {
                throw new IllegalStateException(
                        "DB_SSL_CA certificate could not be loaded", e);
            }
            return withQueryOptions(upgraded, List.<String[]>of(
                    new String[] {"trustCertificateKeyStoreUrl", trustStore.toURI().toString()},
                    new String[] {"trustCertificateKeyStoreType", "PKCS12"},
                    new String[] {"trustCertificateKeyStorePassword", TRUST_STORE_PASSWORD}),
                    "serverSslCert", "trustStore", "trustStoreType", "trustStorePassword");
        }
        throw new IllegalArgumentException("Unsupported JDBC URL prefix");
    }

    /**
     * Rewrites the URL so that it carries exactly one {@code sslMode}
     * option, with the given value.
     *
     * <p>Option names are matched case-insensitively, because providers
     * and driver documentation write both {@code sslMode} and
     * {@code sslmode}. Any existing {@code sslMode} option is removed
     * rather than left alongside a second, conflicting one. All other
     * options are preserved as they were written.
     *
     * @param url  the configured JDBC URL
     * @param mode the SSL mode to force
     * @return the URL with a single sslMode option
     */
    static String withSslMode(String url, String mode) {
        int queryStart = url.indexOf('?');
        if (queryStart < 0) {
            return url + "?sslMode=" + mode;
        }

        return withQueryOptions(url, List.<String[]>of(new String[] {"sslMode", mode}));
    }

    private static String withQueryOptions(String url, List<String[]> additions, String... staleNames) {
        int queryStart = url.indexOf('?');
        String base = queryStart < 0 ? url + "?" : url.substring(0, queryStart + 1);
        List<String> options = new ArrayList<>();
        if (queryStart >= 0) {
            for (String option : url.substring(queryStart + 1).split("&")) {
                if (!option.isEmpty()) {
                    options.add(option);
                }
            }
        }

        for (String[] addition : additions) {
            options.removeIf(option -> {
                int eq = option.indexOf('=');
                String name = eq < 0 ? option : option.substring(0, eq);
                if (name.equalsIgnoreCase(addition[0])) {
                    return true;
                }
                for (String staleName : staleNames) {
                    if (name.equalsIgnoreCase(staleName)) {
                        return true;
                    }
                }
                return false;
            });
        }
        for (String[] addition : additions) {
            options.add(encodeQuery(addition[0]) + "=" + encodeQuery(addition[1]));
        }
        return base + String.join("&", options);
    }

    private static String encodeQuery(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    static String driverClassForUrl(String url) throws SQLException {
        if (url != null && url.startsWith("jdbc:mariadb://")) {
            return MARIADB_DRIVER;
        }
        if (url != null && url.startsWith("jdbc:mysql://")) {
            return MYSQL_DRIVER;
        }
        throw new SQLException("Unsupported JDBC URL prefix; expected jdbc:mariadb:// or jdbc:mysql://");
    }

    /** Builds a PKCS#12 truststore holding the supplied PEM certificate(s). */
    private static File buildTrustStore(String caFile) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);

        int index = 0;
        try (InputStream in = openCertificateSource(caFile)) {
            for (java.security.cert.Certificate cert :
                    java.security.cert.CertificateFactory.getInstance("X.509").generateCertificates(in)) {
                keyStore.setCertificateEntry("ca-" + (index++), cert);
            }
        }
        if (index == 0) {
            throw new IllegalArgumentException("no X.509 certificate found in " + caFile);
        }

        File file = File.createTempFile("quizcraft-truststore", ".p12");
        file.deleteOnExit();
        try (java.io.FileOutputStream out = new FileOutputStream(file)) {
            keyStore.store(out, TRUST_STORE_PASSWORD.toCharArray());
        }
        return file;
    }

    /**
     * Opens the certificate authority file. A value of the form
     * {@code classpath:some/dir/ca.pem} reads from the deployed application
     * archive, which is how a certificate travels inside a Docker image.
     */
    private static InputStream openCertificateSource(String caFile) throws Exception {
        if (caFile.startsWith("classpath:")) {
            String resource = caFile.substring("classpath:".length());
            InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(resource);
            if (in == null) {
                throw new IllegalArgumentException("resource not found on classpath: " + resource);
            }
            return in;
        }
        return new FileInputStream(caFile);
    }

    public static Connection getConnection() throws SQLException {
        try {
            String driverClassName = driverClassForUrl(URL);
            // Load the JDBC driver class
            Class.forName(driverClassName);
            // For jdbc:mysql:// URLs, explicitly register the driver with DriverManager
            // to ensure it's discovered even in environments where the SPI mechanism
            // (e.g., META-INF/services) doesn't work properly (e.g., Tomcat with multiple JDBC drivers)
            if (URL.startsWith("jdbc:mysql://")) {
                try {
                    // Create a new driver instance and register it
                    java.sql.Driver driver = (java.sql.Driver) Class.forName(driverClassName).getDeclaredConstructor().newInstance();
                    // Check if already registered to avoid duplicate registration
                    java.util.Enumeration<java.sql.Driver> driversEnum = java.sql.DriverManager.getDrivers();
                    boolean alreadyRegistered = false;
                    while (driversEnum.hasMoreElements()) {
                        java.sql.Driver d = driversEnum.nextElement();
                        if (d.getClass().getName().equals(driverClassName)) {
                            alreadyRegistered = true;
                            break;
                        }
                    }
                    if (!alreadyRegistered) {
                        java.sql.DriverManager.registerDriver((java.sql.Driver) driver);
                    }
                } catch (Exception e) {
                    // If explicit registration fails, the subsequent DriverManager.getConnection()
                    // will still attempt to use the driver via the already-loaded driver class
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Configured JDBC driver is not available", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
