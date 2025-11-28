package com.student.dao;

import com.student.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDAO {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Taolavodoi@123";
    
    // SQL Queries
    private static final String SQL_AUTHENTICATE = 
        "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
    
    private static final String SQL_UPDATE_LAST_LOGIN = 
        "UPDATE users SET last_login = NOW() WHERE id = ?";
    
    private static final String SQL_GET_BY_ID = 
        "SELECT * FROM users WHERE id = ?";
    
    private static final String SQL_GET_BY_USERNAME = 
        "SELECT * FROM users WHERE username = ?";
    
    private static final String SQL_INSERT = 
        "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";

    // ✅ NEW: SQL for updating password
    private static final String SQL_UPDATE_PASSWORD =
        "UPDATE users SET password = ? WHERE id = ?";

    // Get database connection
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
    
    /**
     * Authenticate user with username and password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        User user = null;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_AUTHENTICATE)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    
                    // Verify password with BCrypt
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        user = mapResultSetToUser(rs);
                        
                        // Update last login time
                        updateLastLogin(user.getId());
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    /**
     * Update user's last login timestamp
     */
    private void updateLastLogin(int userId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_LAST_LOGIN)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(int id) {
        User user = null;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_ID)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        User user = null;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_BY_USERNAME)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    /**
     * Create new user with hashed password
     */
    public boolean createUser(User user) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT)) {
            
            // Hash password before storing
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ NEW: Update user's password (EX8)
     */
    public boolean updatePassword(int userId, String newHashedPassword) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {

            pstmt.setString(1, newHashedPassword);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
    
    /**
     * Test method - Generate hashed password
     */
    public static void main(String[] args) {
        // Generate hash for "password123"
        String plainPassword = "password123";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        System.out.println("Plain: " + plainPassword);
        System.out.println("Hashed: " + hashedPassword);
        
        // Test verification
        boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
        System.out.println("Verification: " + matches);
    }
}
