import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager using JDBC
 * Handles all MySQL connections
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Database credentials - Update these with your MySQL details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Welcome@123";

    private DatabaseConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    /**
     * Singleton pattern: Get the single instance of DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Get the database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
