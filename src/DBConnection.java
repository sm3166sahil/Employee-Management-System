package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection Class
 * Handles establishing and returning a connection to the MySQL database.
 * Uses JDBC DriverManager.
 */
public class DBConnection {

    // =========================================================================
    // DATABASE CONFIGURATION (Change these according to your local MySQL setup)
    // =========================================================================
    private static final String URL = "jdbc:mysql://localhost:3306/employee_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Manju@123"; // Updated MySQL root password

    // JDBC Driver class name (Loaded automatically in modern JDBC, but good to register explicitly)
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    /**
     * Obtains a connection to the database.
     * @return Connection object or null if failed
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Step 1: Explicitly load the MySQL JDBC driver class
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Make sure mysql-connector-j.jar is added to classpath.");
            e.printStackTrace();
        }

        // Step 2: Establish connection via DriverManager
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
