package com.mku.version2;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ConfirmOrderServlet", urlPatterns = {"/confirmorder"})
public class ConfirmOrderServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku";
    private static final String dbUsername = "root";
    private static final String dbPassword = "root1234";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        UserInfo user = (UserInfo) request.getSession().getAttribute("LoggedInUser");
        ArrayList<ProductInfo> products = (ArrayList<ProductInfo>) request.getSession().getAttribute("ProductsInCart");
        double totalAmount = 0.0;

        // Calculate total amount for the products in the cart
        if (products != null && !products.isEmpty()) {
            for (ProductInfo product : products) {
                totalAmount += product.getPrice() * product.getQuantity();
            }
            totalAmount = Math.round(totalAmount * 100.0) / 100.0; // Round to two decimal places
        }

        PaymentInfo paymentInfo = null;
        String paymentQuery = "SELECT * FROM Payment WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
             PreparedStatement pst = conn.prepareStatement(paymentQuery)) {

            if (user != null) {
                pst.setInt(1, user.getUserId());

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        int paymentId = rs.getInt("payment_id");
                        String paymentMethod = rs.getString("payment_method");
                        String cardNumber = rs.getString("card_number");
                        String cardExpiry = rs.getString("card_expiration_date");
                        String paypalEmail = rs.getString("paypal_email");

                        // Initialize PaymentInfo based on the payment method
                        if (paymentMethod.contains("Card")) {
                            paymentInfo = new PaymentInfo(paymentId, paymentMethod, cardNumber, cardExpiry, user.getUserId());
//                            paymentInfo = null;
                        } else if ("paypal".equalsIgnoreCase(paymentMethod)) {
                            paymentInfo = new PaymentInfo(paymentId, paymentMethod, paypalEmail, user.getUserId());
                        }
                    }
                }
            } else {
                request.setAttribute("errorMessage", "User is not logged in.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Set the necessary attributes for the JSP page
            request.setAttribute("paymentInfo", paymentInfo);
            request.setAttribute("products", products);
            request.setAttribute("totalAmount", totalAmount);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/confirmorder.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while retrieving order confirmation details.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to confirm order details before placing it.";
    }
}
