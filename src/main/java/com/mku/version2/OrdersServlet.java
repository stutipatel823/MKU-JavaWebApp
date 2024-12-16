package com.mku.version2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "OrdersServlet", urlPatterns = {"/orders"})
public class OrdersServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku"; // Database URL
    private static final String dbUsername = "root"; // Database username
    private static final String dbPassword = "root1234"; // Database password

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        UserInfo user = (UserInfo) request.getSession().getAttribute("LoggedInUser");
        ArrayList<OrderInfo> orders = new ArrayList<>();

        String query = "SELECT * FROM `Order` WHERE user_id = ? ORDER BY order_date DESC";
        String query2 = "SELECT p.name AS productName, p.price, op.quantity " +
                        "FROM Order_Product op " +
                        "JOIN Product p ON op.product_id = p.product_id " +
                        "WHERE op.order_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
             PreparedStatement pst = conn.prepareStatement(query)) {

            if (user != null) {
                pst.setInt(1, user.getUserId()); // Set the CartID (user ID)
                
                // Execute query
                try (ResultSet rs = pst.executeQuery()) {
                    System.out.println("<orderservlet>: Connected Successfully");

                    while (rs.next()) {
                        int orderId = rs.getInt("order_id");
                        String orderDate = rs.getString("order_date");
                        double amount = rs.getDouble("amount");
                        String transactionDate = rs.getString("transaction_date");
                        String status = rs.getString("status");
                        int userId = rs.getInt("user_id");
                        int paymentId = rs.getInt("payment_id");
                        
                        OrderInfo order = new OrderInfo(orderId,orderDate,amount,transactionDate,status,userId,paymentId);
                        ArrayList<ProductInfo> productsOrdered = new ArrayList<>();
                        
                        try (PreparedStatement pst2 = conn.prepareStatement(query2)) {
                            pst2.setInt(1, orderId);
                            try(ResultSet rs2 = pst2.executeQuery()){
                                while(rs2.next()){
                                    ProductInfo product = new ProductInfo();
                                    
                                    String productName = rs2.getString("productName");
                                    int quantity = rs2.getInt("quantity");
                                    double price = rs2.getDouble("price");
                                    product.setName(productName);
                                    product.setQuantity(quantity);
                                    product.setPrice(price * quantity);
                                    productsOrdered.add(product);
                                }
                            }
                        }
                           
                        order.setProducts(productsOrdered);
                        orders.add(order);
                    }
                }
            } else {
                // Handle case when user is not logged in
                request.setAttribute("errorMessage", "User is not logged in.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Store products in request
            request.setAttribute("orders", orders);

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/orders.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while retrieving orders.");
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
        return "CartServlet to manage user's cart.";
    }
}

