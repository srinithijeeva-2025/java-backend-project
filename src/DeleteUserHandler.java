import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP handler for deleting users
 * Admin only operation
 */
public class DeleteUserHandler implements HttpHandler {
    private UserService userService;
    private SessionManager sessionManager;

    public DeleteUserHandler() {
        this.userService = new UserService();
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handleDeleteUserRequest(exchange);
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
        }
    }

    private void handleDeleteUserRequest(HttpExchange exchange) throws IOException {
        try {
            // Check admin access
            String sessionId = getSessionId(exchange);
            if (!sessionManager.isAdmin(sessionId)) {
                sendErrorResponse(exchange, "Access Denied - Admins Only");
                return;
            }

            // Parse form data
            String body = RequestUtils.readRequestBody(exchange);
            Map<String, String> params = parseFormData(body);

            int userId = Integer.parseInt(params.getOrDefault("userId", "0"));

            // Validate input
            if (userId <= 0) {
                sendErrorResponse(exchange, "Valid user ID is required");
                return;
            }

            // Check if user exists
            User user = userService.getUserById(userId);
            if (user == null) {
                sendErrorResponse(exchange, "User not found");
                return;
            }

            // Delete user
            if (userService.deleteUser(userId)) {
                sendSuccessResponse(exchange, "User deleted successfully!");
                System.out.println("User deleted: " + user.getUsername());
            } else {
                sendErrorResponse(exchange, "Error deleting user");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(exchange, "Invalid user ID format");
        } catch (Exception e) {
            System.out.println("Delete user handler error: " + e.getMessage());
            sendErrorResponse(exchange, "An error occurred while deleting the user");
        }
    }

    private String getSessionId(HttpExchange exchange) {
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
                if (cookie.trim().startsWith("sessionId=")) {
                    return cookie.trim().substring(10);
                }
            }
        }
        return null;
    }

    private Map<String, String> parseFormData(String body) throws Exception {
        Map<String, String> params = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = URLDecoder.decode(keyValue[1], "UTF-8");
                params.put(key, value);
            }
        }
        return params;
    }

    private void sendSuccessResponse(HttpExchange exchange, String message) throws IOException {
        // Redirect back to the dashboard with a flash message
        String encoded = URLEncoder.encode(message, "UTF-8");
        String location = "/public/dashboard.html?flash=" + encoded;
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
        String response = "<html><body><h1>Error</h1><p>" + errorMessage + 
                         "</p><a href='/public/deleteUser.html'>Try Again</a></body></html>";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
