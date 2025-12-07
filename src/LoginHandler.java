import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP handler for login requests
 * Validates credentials and creates session
 */
public class LoginHandler implements HttpHandler {
    private UserService userService;
    private SessionManager sessionManager;

    public LoginHandler() {
        this.userService = new UserService();
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handleLoginRequest(exchange);
        } else if ("GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(302, 0);
            exchange.getResponseHeaders().set("Location", "/public/login.html");
            exchange.close();
        }
    }

    private void handleLoginRequest(HttpExchange exchange) throws IOException {
        try {
            // Parse form data
            String body = RequestUtils.readRequestBody(exchange);
            Map<String, String> params = parseFormData(body);

            String username = params.getOrDefault("username", "");
            String password = params.getOrDefault("password", "");

            // Authenticate user
            User user = userService.authenticateUser(username, password);

            if (user != null) {
                // Create session
                String sessionId = sessionManager.createSession(user.getUsername(), user.getRole());
                
                // Set session cookie and return a small HTML response that stores
                // the user info in sessionStorage, then navigates to the dashboard.
                exchange.getResponseHeaders().set("Set-Cookie", "sessionId=" + sessionId + "; Path=/");

                // Build a safe JSON string for the client (escape single quotes)
                String userJson = "{\"username\":\"" + user.getUsername() + "\",\"role\":\"" + user.getRole() + "\"}";
                userJson = userJson.replace("'", "\\'");

                // Also prepare a flash message encoded for the redirect URL
                String flashMsg = "Login successful for user: " + user.getUsername();
                String encodedFlash = URLEncoder.encode(flashMsg, "UTF-8");

                String response = "<html><body><script>" +
                                  "try { sessionStorage.setItem('user', '" + userJson + "'); } catch(e){};" +
                                  "window.location.href='/public/dashboard.html?flash=" + encodedFlash + "';" +
                                  "</script></body></html>";

                exchange.getResponseHeaders().set("Content-Type", "text/html");
                byte[] respBytes = response.getBytes();
                exchange.sendResponseHeaders(200, respBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(respBytes);
                os.close();

                System.out.println("Login successful for user: " + username);
            } else {
                // Login failed - return error
                sendErrorResponse(exchange, "Invalid username or password. Try again.");
                System.out.println("Login failed for user: " + username);
            }
        } catch (Exception e) {
            System.out.println("Login handler error: " + e.getMessage());
            sendErrorResponse(exchange, "An error occurred during login.");
        }
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

    private void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
        String response = "<html><body><h1>Login Error</h1><p>" + errorMessage + 
                         "</p><a href='/public/login.html'>Try Again</a></body></html>";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
