import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for user operations
 * Handles all database operations related to users using JDBC
 */
public class UserService {
    private Connection connection;

    public UserService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Authenticate user by checking username and password
     */
    public User authenticateUser(String username, String password) {
        try {
            String query = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("role")
                );
                resultSet.close();
                statement.close();
                return user;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get user by ID
     */
    public User getUserById(int id) {
        try {
            String query = "SELECT id, username, password, role FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("role")
                );
                resultSet.close();
                statement.close();
                return user;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            String query = "SELECT id, username, password, role FROM users";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("role")
                );
                users.add(user);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Add new user
     */
    public boolean addUser(User user) {
        try {
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());

            int result = statement.executeUpdate();
            statement.close();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update existing user
     */
    public boolean updateUser(int id, User user) {
        try {
            String query = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setInt(4, id);

            int result = statement.executeUpdate();
            statement.close();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Delete user by ID
     */
    public boolean deleteUser(int id) {
        try {
            String query = "DELETE FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            int result = statement.executeUpdate();
            statement.close();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if username already exists
     */
    public boolean userExists(String username) {
        try {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            resultSet.close();
            statement.close();
            return exists;
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }
}
