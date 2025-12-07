import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Small helper utilities for request handling to keep compatibility with Java 8
 */
public class RequestUtils {
    /**
     * Read the request body fully and return it as a UTF-8 string.
     */
    public static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
