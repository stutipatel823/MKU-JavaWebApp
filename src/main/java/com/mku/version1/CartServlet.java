package com.mku.version1;

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

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku"; // Database URL
    private static final String dbUsername = "root"; // Database username
    private static final String dbPassword = "root1234"; // Database password

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        UserInfo user = (UserInfo) request.getSession().getAttribute("LoggedInUser");
        ArrayList<ProductInfo> products = new ArrayList<>();

        String query = "SELECT p.*, cp.quantity " +
                        "FROM Cart c " +
                        "JOIN Cart_Product cp ON c.cart_id = cp.cart_id " +
                        "JOIN Product p ON cp.product_id = p.product_id " +
                        "WHERE c.user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
             PreparedStatement pst = conn.prepareStatement(query)) {

            if (user != null) {
                pst.setInt(1, user.getUserId()); // Set the CartID (user ID)

                // Execute query
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        ProductInfo product = new ProductInfo(); // Assuming ProductInfo has a no-arg constructor
                        product.setProductId(rs.getInt("product_id"));
                        product.setAdminId(rs.getInt("admin_id"));
                        product.setName(rs.getString("name")); // Ensure these column names are correct
                        product.setBrand(rs.getString("brand"));
                        product.setPrice(rs.getDouble("price")*rs.getInt("quantity"));
                        product.setImageURL(rs.getString("imageURL")); // Assuming you have an imageURL column
                        product.setQuantity(rs.getInt("quantity"));
                        product.setStock(rs.getInt("stock"));
                        product.setAvailability(rs.getBoolean("availability")); // Assuming availability is a boolean
                        products.add(product);
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
            request.setAttribute("ProductsInCart", products);

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/cart.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while retrieving the cart.");
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
