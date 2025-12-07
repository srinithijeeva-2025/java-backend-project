import java.util.HashMap;
import java.util.UUID;

/**
 * Session management for storing user sessions in memory
 * Simplified approach using HashMap - for production, use a dedicated session store
 */
public class SessionManager {
    private static SessionManager instance;
    private HashMap<String, SessionData> sessions;

    private static class SessionData {
        String username;
        String role;
        long createdTime;

        SessionData(String username, String role) {
            this.username = username;
            this.role = role;
            this.createdTime = System.currentTimeMillis();
        }
    }

    private SessionManager() {
        this.sessions = new HashMap<>();
    }

    /**
     * Singleton pattern: Get the single instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Create a new session for a user
     */
    public String createSession(String username, String role) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new SessionData(username, role));
        System.out.println("Session created for user: " + username);
        return sessionId;
    }

    /**
     * Get session data by session ID
     */
    public SessionData getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Check if session exists
     */
    public boolean isValidSession(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    /**
     * Get user from session
     */
    public String getUsername(String sessionId) {
        SessionData data = sessions.get(sessionId);
        return data != null ? data.username : null;
    }

    /**
     * Get user role from session
     */
    public String getUserRole(String sessionId) {
        SessionData data = sessions.get(sessionId);
        return data != null ? data.role : null;
    }

    /**
     * Invalidate a session (logout)
     */
    public void invalidateSession(String sessionId) {
        sessions.remove(sessionId);
        System.out.println("Session invalidated: " + sessionId);
    }

    /**
     * Check if user has admin role
     */
    public boolean isAdmin(String sessionId) {
        String role = getUserRole(sessionId);
        return role != null && role.equals("ADMIN");
    }
}
