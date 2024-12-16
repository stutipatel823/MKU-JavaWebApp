package com.mku.persistence;

import com.mku.helper.UserInfo;
import java.sql.*;

public class User_CRUD {

//    private static final String DB_URL = "jdbc:mysql://localhost:3306/Frontend_MKU"; // Database URL
    private static final String DB_USERNAME = "root"; // Database username
    private static final String DB_PASSWORD = "root1234"; // Database password

    // Get database connection
    private static Connection getConnection() throws SQLException {
        // Ensure both DB_URL and DB_PORT are set in environment variables
        String dbUrl = System.getenv("DB_URL");
        String dbPort = System.getenv("DB_PORT");
        if (dbUrl == null || dbUrl.isEmpty() || dbPort == null || dbPort.isEmpty()) {
            throw new IllegalStateException("DB_URL or DB_PORT environment variable is not set");
        }

        // Construct the connection string properly
        String DB_URL = "jdbc:mysql://" + dbUrl + ":" + dbPort + "/Frontend_MKU";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }

        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }


    // Read user by email and password
    public static UserInfo readUser(String email, String password) throws SQLException {
        UserInfo user = null;
        String query = "SELECT * FROM User WHERE LOWER(email) = LOWER(?) AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                user = new UserInfo();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstname(rs.getString("firstname"));
                user.setLastname(rs.getString("lastname"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhonenumber(rs.getString("phonenumber"));
                user.setStreet(rs.getString("street") != null ? rs.getString("street") : "");
                user.setCity(rs.getString("city") != null ? rs.getString("city") : "");
                user.setProvince(rs.getString("province") != null ? rs.getString("province") : "");
                user.setCountry(rs.getString("country") != null ? rs.getString("country") : "");
                user.setPostalCode(rs.getString("postalcode") != null ? rs.getString("postalcode") : "");
            } else {
                throw new SQLException("User not found with the provided credentials.");
            }

        } catch (SQLException e) {
            System.err.println("Error reading user: " + e.getMessage());
            throw e; // Re-throw the exception
        }

        return user;
    }

    // Create new user
    public static int createUser(String firstname, String lastname, String email, String password,
                                  String confirmPassword) throws SQLException {
        // Check if password matches
        if (!password.equals(confirmPassword)) {
            throw new SQLException("Password mismatch");
        }

        if (isEmailUnique(email)) {
            int userId = insertUser(firstname, lastname, email, password);
            return userId;
        } else {
            throw new SQLException("Email is already in use");
        }
    }

    // Check if email is unique
    public static boolean isEmailUnique(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM User WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            return rs.next() && rs.getInt(1) == 0; // Return true if email is unique

        } catch (SQLException e) {
            System.err.println("Error checking email uniqueness: " + e.getMessage());
            throw e; // Re-throw the exception
        }
    }

    // Insert new user into the database
    private static int insertUser(String firstname, String lastname, String email, String password) throws SQLException {
        String insertQuery = "INSERT INTO User (firstname, lastname, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {  // Request generated keys

            pst.setString(1, firstname);
            pst.setString(2, lastname);
            pst.setString(3, email);
            pst.setString(4, password);

            pst.executeUpdate(); // Execute the update, if it fails, SQLException will be thrown

            // Retrieve the auto-generated user_id
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated user_id
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            throw e; // Re-throw the exception
        }
    }

}
