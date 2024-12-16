package com.mku.version2;

import java.io.IOException;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "PaymentMethodServlet", urlPatterns = {"/paymentMethod"})
public class PaymentMethodServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku"; // Database URL
    private static final String DB_USERNAME = "root"; // Database username
    private static final String DB_PASSWORD = "root1234"; // Database password
    
    private static final Logger LOGGER = Logger.getLogger(PaymentMethodServlet.class.getName());
    private boolean hasPaymentMethod = false;
    
    // Helper method to get a database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    // Helper method to handle payment method update or insertion
    private void updatePaymentMethod(int userId, String paymentType, String cardNumber, String expDate, String paypalEmail) throws SQLException {
        String query;

        // Check if the user has an existing payment method to decide between INSERT and UPDATE
        if (hasPaymentMethod) {
            // Update existing payment method
            if (paymentType.equals("PayPal")) {
                query = "UPDATE Payment SET payment_method = ?, card_number = NULL, card_expiration_date = NULL, paypal_email = ? WHERE user_id = ?";
            } else {
                query = "UPDATE Payment SET payment_method = ?, card_number = ?, card_expiration_date = ?, paypal_email = NULL WHERE user_id = ?";
            }
        } else {
            // Insert new payment method
            if (paymentType.equals("PayPal")) {
                query = "INSERT INTO Payment (payment_method, user_id, paypal_email, card_number, card_expiration_date) VALUES (?, ?, ?, NULL, NULL)";
            } else {
                query = "INSERT INTO Payment (payment_method, user_id, card_number, card_expiration_date, paypal_email) VALUES (?, ?, ?, ?, NULL)";
            }
        }

        // Execute the prepared statement with the correct parameters
        try (Connection conn = getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
            if (hasPaymentMethod) {
                // Set parameters for UPDATE
                pst.setString(1, paymentType);  
                if (paymentType.equals("PayPal")) {
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
                if (paymentType.equals("PayPal")) {
                    pst.setString(3, paypalEmail); 
                } else {
                    pst.setString(3, cardNumber);  
                    pst.setString(4, expDate);     
                }
            }

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Payment method updated for user ID: " + userId);
            } else {
                System.out.println("No changes made to the payment method for user ID: " + userId);
            }
        }
    }





    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        UserInfo user = (UserInfo) request.getSession().getAttribute("LoggedInUser");
        PaymentInfo payment = null;

        // Display User's Payment Method if exists
        String query = "SELECT * FROM Payment WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, user.getUserId());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int paymentId = rs.getInt("payment_id");
                String paymentMethod = rs.getString("payment_method");
                String cardNumber = rs.getString("card_number");
                String expDate = rs.getString("card_expiration_date");
                String paypalEmail = rs.getString("paypal_email");
                int userId = rs.getInt("user_id");

                if (paymentMethod.equals("PayPal")) {
                    payment = new PaymentInfo(paymentId, paymentMethod, paypalEmail, userId);
                } else {
                    payment = new PaymentInfo(paymentId, paymentMethod, cardNumber, expDate, userId);
                }
                hasPaymentMethod = true;
            }
            
            request.setAttribute("payment", payment);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while fetching payment method", e);
        }

        // Forward to JSP page
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/paymentmethod.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error in GET request", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String paymentType = request.getParameter("paymentType");
        int userId = ((UserInfo) request.getSession().getAttribute("LoggedInUser")).getUserId();
        String cardNumber = request.getParameter("cardNumber");
        String expDate = request.getParameter("expDate");
        String paypalEmail = request.getParameter("paypalEmail");
        System.out.println(expDate);
        try {
            // Update or insert the payment method
            updatePaymentMethod(userId, paymentType, cardNumber, expDate, paypalEmail);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error while updating payment method", ex);
        }

        // Redirect back to the payment method page after submission
        response.sendRedirect(request.getContextPath() + "/paymentMethod");
    }

    @Override
    public String getServletInfo() {
        return "Servlet to handle payment method submission.";
    }
}
