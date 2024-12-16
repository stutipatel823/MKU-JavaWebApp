package com.mku.persistence;

import com.mku.helper.UserInfo;
import java.sql.*;

public class User_CRUD {

// //    private static final String DB_URL = "jdbc:mysql://localhost:3306/Frontend_MKU"; // Database URL
     private static final String DB_USERNAME = "root"; // Database username
     private static final String DB_PASSWORD = "root1234"; // Database password

     // Get database connection
     private static Connection getConnection() throws SQLException {
         String connection = System.getenv("DB_URL") + ":" + System.getenv("DB_PORT");
         String DB_URL = "jdbc:mysql://" + connection + "/Frontend_MKU";
         if (connection.isEmpty() || connection==null) {
            throw new IllegalStateException("DB_URL or DB_PORT environment variable is not set");
        }
         try {
             Class.forName("com.mysql.cj.jdbc.Driver");
         } catch (ClassNotFoundException e) {
             throw new SQLException("MySQL Driver not found", e);
         }
         return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
     }

//    // Default values for username and password
//    private static final String DB_USERNAME = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "root";
//    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "root1234";
//
//    // Get database connection
//    private static Connection getConnection() throws SQLException {
//        String dbUrl = System.getenv("DB_URL");  // Get the DB URL from environment variable
//        if (dbUrl == null || dbUrl.isEmpty()) {
//            throw new IllegalStateException("DB_URL environment variable is not set");
//        }
//        // Get the DB port from the environment, or use a default if not set
//        String dbPort = System.getenv("DB_PORT");
//        if (dbPort == null || dbPort.isEmpty()) {
//            dbPort = "3306"; // Default MySQL port if not set
//        }
//        // Assuming the DB service name is "db" and MySQL is on port 3306 internally in the Docker network
//        String DB_URL = "jdbc:mysql://" + dbUrl + ":" + dbPort + "/Frontend_MKU";  // Use dynamic port from environment
//        System.out.println("dbUrl: " + dbUrl);
//
//        // Try to load the MySQL driver
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new SQLException("MySQL Driver not found", e);
//        }
//
//        // Return the database connection
//        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
//    }

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
