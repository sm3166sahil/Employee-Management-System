package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserDAO Class (Data Access Object)
 * Handles database operations related to user authentication and roles.
 */
public class UserDAO {

    /**
     * Authenticates a user by matching username and password in the database.
     * @param username The entered username
     * @param password The entered password
     * @return User object with role if credentials are valid, or null if invalid
     * @throws SQLException if database query fails
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        }
        return null; // Invalid credentials
    }
}
