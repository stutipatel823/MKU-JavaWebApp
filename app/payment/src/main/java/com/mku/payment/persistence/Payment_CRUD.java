package com.mku.payment.persistence;

import com.mku.payment.helper.PaymentInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Payment_CRUD {
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/Payment_MKU";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root1234";

    private static Connection getConnection() throws SQLException {
        String dbUrl = System.getenv("DB_URL");
        String dbPort = System.getenv("DB_PORT");
        if (dbUrl == null || dbUrl.isEmpty() || dbPort == null || dbPort.isEmpty()) {
            throw new IllegalStateException("DB_URL or DB_PORT environment variable is not set");
        }

        String DB_URL = "jdbc:mysql://" + dbUrl +":"+ dbPort + "/Payment_MKU";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    // Retrieve all payment methods for a specific user
    public static PaymentInfo getPaymentMethodByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM Payment WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int paymentId = rs.getInt("payment_id");
                    String paymentMethod = rs.getString("payment_method");
                    String cardNumber = rs.getString("card_number");
                    String expDate = rs.getString("card_expiration_date");
                    String paypalEmail = rs.getString("paypal_email");

                    if ("PayPal".equalsIgnoreCase(paymentMethod)) {
                        return new PaymentInfo(paymentId, paymentMethod, paypalEmail, userId);
                    } else {
                        return new PaymentInfo(paymentId, paymentMethod, cardNumber, expDate, userId);
                    }
                }
            }
        }
        return null; // Return null if no payment method is found
    }


    // Add a new payment method
    public static int createPaymentMethod(int userId, String paymentType, String cardNumber, String expDate, String paypalEmail) throws SQLException {
        String query = "INSERT INTO Payment (payment_method, user_id, card_number, card_expiration_date, paypal_email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, paymentType);
            stmt.setInt(2, userId);
            if ("PayPal".equalsIgnoreCase(paymentType)) {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.VARCHAR);
                stmt.setString(5, paypalEmail);
            } else {
                stmt.setString(3, cardNumber);
                stmt.setString(4, expDate);
                stmt.setNull(5, Types.VARCHAR);
            }

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated payment ID
                } else {
                    throw new SQLException("Failed to retrieve payment ID.");
                }
            }
        }
    }

    public static boolean updatePaymentMethod(int userId, String paymentType, String cardNumber, String expDate, String paypalEmail, boolean hasPaymentMethod) throws SQLException {
        String query;

        // Determine if updating or inserting based on `hasPaymentMethod`
        if (hasPaymentMethod) {
            // Update existing payment method
            if ("PayPal".equalsIgnoreCase(paymentType)) {
                query = "UPDATE Payment SET payment_method = ?, card_number = NULL, card_expiration_date = NULL, paypal_email = ? WHERE user_id = ?";
            } else {
                query = "UPDATE Payment SET payment_method = ?, card_number = ?, card_expiration_date = ?, paypal_email = NULL WHERE user_id = ?";
            }
        } else {
            // Insert new payment method
            if ("PayPal".equalsIgnoreCase(paymentType)) {
                query = "INSERT INTO Payment (payment_method, user_id, paypal_email, card_number, card_expiration_date) VALUES (?, ?, ?, NULL, NULL)";
            } else {
                query = "INSERT INTO Payment (payment_method, user_id, card_number, card_expiration_date, paypal_email) VALUES (?, ?, ?, ?, NULL)";
            }
        }

        // Execute the query
        try (Connection conn = getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
            if (hasPaymentMethod) {
                // Set parameters for UPDATE
                pst.setString(1, paymentType);
                if ("PayPal".equalsIgnoreCase(paymentType)) {
                    pst.setString(2, paypalEmail);
                    pst.setInt(3, userId);
                } else {
                    pst.setString(2, cardNumber);
                    pst.setString(3, expDate);
                    pst.setInt(4, userId);
                }
            } else {
                // Set parameters for INSERT
                pst.setString(1, paymentType);
                pst.setInt(2, userId);
                if ("PayPal".equalsIgnoreCase(paymentType)) {
                    pst.setString(3, paypalEmail);
                } else {
                    pst.setString(3, cardNumber);
                    pst.setString(4, expDate);
                }
            }

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was affected
        }
    }


    // Delete a payment method by ID
    public static boolean deletePaymentMethod(int paymentId) throws SQLException {
        String query = "DELETE FROM Payment WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, paymentId);
            return stmt.executeUpdate() > 0; // Return true if at least one row was deleted
        }
    }
    
    // Retrieve all payment methods
    public static List<PaymentInfo> getAllPaymentMethods() throws SQLException {
        List<PaymentInfo> paymentMethods = new ArrayList<>();
        String query = "SELECT * FROM Payment";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int paymentId = rs.getInt("payment_id");
                String paymentMethod = rs.getString("payment_method");
                String cardNumber = rs.getString("card_number");
                String expDate = rs.getString("card_expiration_date");
                String paypalEmail = rs.getString("paypal_email");
                int userId = rs.getInt("user_id");
                if ("PayPal".equalsIgnoreCase(paymentMethod)) {
                    paymentMethods.add(new PaymentInfo(paymentId, paymentMethod, paypalEmail, userId));
                } else {
                    paymentMethods.add(new PaymentInfo(paymentId, paymentMethod, cardNumber, expDate, userId));
                }
            }
        }
        return paymentMethods;
    }

}
