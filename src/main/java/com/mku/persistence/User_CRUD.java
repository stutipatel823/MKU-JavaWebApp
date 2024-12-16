package com.mku.persistence;

import com.mku.helper.UserInfo;
import java.sql.*;

public class User_CRUD {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku"; // Database URL
    private static final String DB_USERNAME = "root"; // Database username
    private static final String DB_PASSWORD = "root1234"; // Database password

    // Get database connection
    private static Connection getConnection() throws SQLException {
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
    public static void createUser(String firstname, String lastname, String email, String password,
                                  String confirmPassword) throws SQLException {
        // Check if password matches
        if (!password.equals(confirmPassword)) {
            throw new SQLException("Password mismatch");
        }

        if (isEmailUnique(email)) {
            insertUser(firstname, lastname, email, password);
        } else {
            throw new SQLException("Email is already in use");
        }
    }

    // Check if email is unique
    private static boolean isEmailUnique(String email) throws SQLException {
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
    private static void insertUser(String firstname, String lastname, String email, String password) throws SQLException {
        String insertQuery = "INSERT INTO User (firstname, lastname, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(insertQuery)) {

            pst.setString(1, firstname);
            pst.setString(2, lastname);
            pst.setString(3, email);
            pst.setString(4, password);

            pst.executeUpdate(); // Simply execute the update, if it fails, SQLException will be thrown

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            throw e; // Re-throw the exception
        }
    }
}
