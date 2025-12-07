import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Main application entry point
 * Starts the HTTP server and registers request handlers
 */
public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            // Create HTTP server on localhost:8080
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            System.out.println("Server starting on http://localhost:" + PORT);

            // Register handlers for different routes
            server.createContext("/login", new LoginHandler());
            server.createContext("/addUser", new AddUserHandler());
            server.createContext("/editUser", new EditUserHandler());
            server.createContext("/deleteUser", new DeleteUserHandler());
            server.createContext("/users", new UsersHandler());

            // Serve static files (HTML, CSS)
            server.createContext("/public", new StaticFileHandler());

            // Start server
            server.setExecutor(null); // Use default executor
            server.start();

            System.out.println("Server is running on http://localhost:" + PORT);
            System.out.println("Access the application at http://localhost:" + PORT + "/public/login.html");
            System.out.println("Demo credentials:");
            System.out.println("  Admin: admin / admin123");
            System.out.println("  User: user / user123");

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
