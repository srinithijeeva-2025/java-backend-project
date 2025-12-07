import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * HTTP handler to return the list of users as JSON
 */
public class UsersHandler implements HttpHandler {
    private UserService userService = new UserService();
    private SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Basic session check
            String sessionId = getSessionId(exchange);
            if (sessionId == null || !sessionManager.isValidSession(sessionId)) {
                String resp = "{\"error\":\"unauthorized\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(401, resp.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(resp.getBytes());
                os.close();
                return;
            }

            List<User> users = userService.getAllUsers();
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            boolean first = true;
            for (User u : users) {
                if (!first) sb.append(',');
                first = false;
                sb.append('{')
                  .append("\"id\":").append(u.getId()).append(',')
                  .append("\"username\":\"").append(escapeJson(u.getUsername())).append("\",")
                  .append("\"role\":\"").append(escapeJson(u.getRole())).append("\"")
                  .append('}');
            }
            sb.append(']');

            String response = sb.toString();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] bytes = response.getBytes("UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (Exception e) {
            String resp = "{\"error\":\"internal\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, resp.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp.getBytes());
            os.close();
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

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
