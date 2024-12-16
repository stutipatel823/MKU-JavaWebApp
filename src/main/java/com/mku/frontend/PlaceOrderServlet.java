package com.mku.frontend;

import com.mku.helper.ProductInfo;
import com.mku.helper.UserInfo;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PlaceOrderServlet", urlPatterns = { "/placeorder" })
public class PlaceOrderServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Database connection details
        String DB_URL = "jdbc:mysql://localhost:3306/mku";
        String dbUsername = "root";
        String dbPassword = "root1234";

        PrintWriter out = response.getWriter();

        // Retrieve cart products from the request (make sure it's an ArrayList of ProductInfo)
        ArrayList<ProductInfo> products = (ArrayList<ProductInfo>) request.getSession().getAttribute("ProductsInCart");
        UserInfo user = (UserInfo) request.getSession().getAttribute("LoggedInUser");
        int userId = user.getUserId();

        // Ensure products exist in the cart
        if (products == null || products.isEmpty()) {
            out.println("No products found in the cart.");
            System.out.println("No products found in the cart.");
            return;
        }

        // Calculate totalAmount based on the products in the cart
        double totalAmount = 0.0;
        for (ProductInfo product : products) {
            totalAmount += product.getPrice() * product.getQuantity();
        }

        String status = "Pending"; // Default status

        // SQL queries
        String checkPaymentIdQuery = "SELECT payment_id FROM Payment WHERE user_id = ?";
        String insertOrderQuery = "INSERT INTO `Order` (order_date, amount, transaction_date, status, user_id, cart_id, payment_id) VALUES (NOW(), ?, NOW(), ?, ?, ?, ?)";
        String insertOrderProductQuery = "INSERT INTO Order_Product (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword)) {

                // Prepare the statements
                try (PreparedStatement pst1 = conn.prepareStatement(checkPaymentIdQuery);
                     PreparedStatement pst2 = conn.prepareStatement(insertOrderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                     PreparedStatement pst3 = conn.prepareStatement(insertOrderProductQuery)) {

                    // Fetch payment ID for the user
                    pst1.setInt(1, userId);
                    try (ResultSet rs = pst1.executeQuery()) {
                        int paymentId = -1;

                        if (rs.next()) {
                            paymentId = rs.getInt("payment_id");
                            status = "Shipped";
                            System.out.println(paymentId);
                        }

                        if (paymentId == -1) {
                            out.println("No payment records found for the user.");
                            return; // Exit if no valid payment is found
                        }

                        // Insert order into Order table
                        pst2.setDouble(1, totalAmount);
                        pst2.setString(2, status);
                        pst2.setInt(3, userId);
                        pst2.setInt(4, userId);  // Assuming cartId is 0 or can be retrieved
                        pst2.setInt(5, paymentId);
                        pst2.executeUpdate();

                        // Retrieve generated order ID
                        int orderId;
                        try (ResultSet generatedKeys = pst2.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                orderId = generatedKeys.getInt(1);
                            } else {
                                throw new SQLException("Failed to retrieve order ID.");
                            }
                        }

                        // Insert each product in the Order_Product table
                        for (ProductInfo product : products) {
                            pst3.setInt(1, orderId);
                            pst3.setInt(2, product.getProductId());
                            pst3.setInt(3, product.getQuantity());
                            pst3.executeUpdate();
                        }

                        // Redirect to orders page after successful order placement
                        response.sendRedirect(request.getContextPath() + "/orders");

                    } catch (SQLException e) {
                        e.printStackTrace();
                        out.println("An error occurred while placing the order.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    out.println("SQL error occurred during the transaction.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Database connection failed.");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.println("Database driver not found.");
        } finally {
            out.close();
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
        return "Place Order Servlet";
    }
}
