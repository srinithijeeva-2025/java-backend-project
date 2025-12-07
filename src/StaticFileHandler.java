import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * HTTP handler for serving static files (HTML, CSS, etc.)
 */
public class StaticFileHandler implements HttpHandler {
    private static final String PUBLIC_DIR = "public";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        
        // Remove "/public" prefix
        String filePath = requestPath.substring(7); // Remove "/public"
        if (filePath.isEmpty() || filePath.equals("/")) {
            filePath = "login.html";
        }

        // Remove any leading slash so Paths.get joins correctly
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }

        Path file = Paths.get(PUBLIC_DIR, filePath).normalize();

        // Ensure the requested file is inside the PUBLIC_DIR to prevent traversal
        Path publicDirReal = Paths.get(PUBLIC_DIR).toRealPath();
        Path fileReal;
        try {
            fileReal = file.toRealPath();
        } catch (IOException e) {
            sendError(exchange, 404, "File Not Found");
            return;
        }

        if (!fileReal.startsWith(publicDirReal)) {
            sendError(exchange, 403, "Access Denied");
            return;
        }

        // Determine content type
        String contentType = getContentType(file);
        
        try {
            byte[] fileBytes = Files.readAllBytes(fileReal);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        } catch (IOException e) {
            sendError(exchange, 500, "Internal Server Error");
        }
    }

    private String getContentType(Path file) {
        String fileName = file.getFileName().toString();
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = "<html><body><h1>" + statusCode + " " + message + "</h1></body></html>";
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
