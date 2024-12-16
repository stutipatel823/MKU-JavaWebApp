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

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku"; // Database URL
    private static final String dbUsername = "root"; // Database username
    private static final String dbPassword = "root1234"; // Database password

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            UserInfo user = (UserInfo) request.getSession().getAttribute("LoggedInUser");

            if (user != null) {
                deleteProduct(user.getUserId(), productId, response);
            } else {
                response.sendRedirect("login.jsp"); // Redirect to login if not logged in
            }
        } else {
            loadCartItems(request, response);
        }
    }

    private void deleteProduct(int userId, int productId, HttpServletResponse response) throws IOException {
        String deleteQuery = "DELETE FROM Cart_Product WHERE cart_id = ? AND product_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
             PreparedStatement pst = conn.prepareStatement(deleteQuery)) {
            pst.setInt(1, userId); // assuming cart_id is equivalent to user_id
            pst.setInt(2, productId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found in cart.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete product.");
        }
    }

    private void loadCartItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                pst.setInt(1, user.getUserId());

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        ProductInfo product = new ProductInfo();
                        product.setProductId(rs.getInt("product_id"));
                        product.setAdminId(rs.getInt("admin_id"));
                        product.setName(rs.getString("name"));
                        product.setBrand(rs.getString("brand"));
                        product.setPrice(rs.getDouble("price") * rs.getInt("quantity"));
                        product.setImageURL(rs.getString("imageURL"));
                        product.setQuantity(rs.getInt("quantity"));
                        product.setStock(rs.getInt("stock"));
                        product.setAvailability(rs.getBoolean("availability"));
                        products.add(product);
                    }
                }
            } else {
                request.setAttribute("errorMessage", "User is not logged in.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
                dispatcher.forward(request, response);
                return;
            }

            request.getSession().setAttribute("ProductsInCart", products);
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
        return "CartServlet to manage user's cart, including deletion of items.";
    }
}
