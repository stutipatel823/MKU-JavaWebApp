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

@WebServlet(name = "SearchProductsServlet", urlPatterns = {"/searchproducts"})
public class SearchProductsServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku"; 
    private static final String DB_USERNAME = "root"; 
    private static final String DB_PASSWORD = "root1234"; 

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            UserInfo user = (UserInfo) request.getSession().getAttribute("LoggedInUser");

            if (user != null) {
                addProduct(user.getUserId(), productId, response);
            } else {
                response.sendRedirect("login.jsp"); // Redirect to login if not logged in
            }
        } else {
            search(request, response);  // Use the new search method for product searching
        }
    }

    private void addProduct(int userId, int productId, HttpServletResponse response) throws IOException {
        String insertQuery = "INSERT INTO Cart_Product (cart_id, product_id, quantity) VALUES (?, ?, 1) " +
                             "ON DUPLICATE KEY UPDATE quantity = quantity + 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(insertQuery)) {

            pst.setInt(1, userId); 
            pst.setInt(2, productId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add product to cart.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add product.");
        }
    }

    private void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<ProductInfo> products = new ArrayList<>();

        // Get search, brand, and category parameters from request
        String searchInput = request.getParameter("searchInput");
        String brandSelect = request.getParameter("brandSelect");
        String categorySelect = request.getParameter("categorySelect");

        StringBuilder query = new StringBuilder("SELECT * FROM Product WHERE 1=1");
        boolean isSearchOrFilterApplied = false;

        // Apply search filter
        if (searchInput != null && !searchInput.isEmpty()) {
            query.append(" AND name LIKE ?");
            isSearchOrFilterApplied = true;
        }

        // Apply brand filter
        if (brandSelect != null && !brandSelect.isEmpty() && !brandSelect.equals("*")) {
            query.append(" AND brand = ?");
            isSearchOrFilterApplied = true;
        }

        // Apply category filter
        if (categorySelect != null && !categorySelect.isEmpty() && !categorySelect.equals("*")) {
            query.append(" AND category = ?");
            isSearchOrFilterApplied = true;
        }

        // If no search or filter is applied, retrieve all products
        if (!isSearchOrFilterApplied) {
            query = new StringBuilder("SELECT * FROM Product"); // Reset query to get all products
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;

            // Set parameters for search, brand, and category if applied
            if (searchInput != null && !searchInput.isEmpty()) {
                pst.setString(paramIndex++, "%" + searchInput + "%"); // search by name
            }

            if (brandSelect != null && !brandSelect.isEmpty() && !brandSelect.equals("*")) {
                pst.setString(paramIndex++, brandSelect); // filter by brand
            }

            if (categorySelect != null && !categorySelect.isEmpty() && !categorySelect.equals("*")) {
                pst.setString(paramIndex++, categorySelect); // filter by category
            }

            // Execute the query
            ResultSet rs = pst.executeQuery();

            // Process the result set and create product list
            while (rs.next()) {
                ProductInfo product = new ProductInfo();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setBrand(rs.getString("brand"));
                product.setCategory(rs.getString("category"));
                product.setPrice(rs.getDouble("price"));
                product.setStock(rs.getInt("stock"));
                product.setImageURL(rs.getString("imageURL"));
                product.setAvailability(rs.getBoolean("availability"));
                products.add(product);
            }

            // Set products in request attributes
            request.setAttribute("AllProducts", products);
            request.setAttribute("categoryValue", categorySelect);
            request.setAttribute("brandValue", brandSelect);

            // Populate distinct brands and categories for filtering options
            setBrandAndCategoryAttributes(request, conn);

            // Forward to JSP for rendering
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/searchproducts.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while retrieving the products.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void setBrandAndCategoryAttributes(HttpServletRequest request, Connection conn) {
        try {
            ArrayList<String> brands = new ArrayList<>();
            try (PreparedStatement brandStmt = conn.prepareStatement("SELECT DISTINCT Brand FROM Product");
                 ResultSet brandRs = brandStmt.executeQuery()) {
                while (brandRs.next()) {
                    brands.add(brandRs.getString("Brand"));
                }
            }
            request.setAttribute("brands", brands);

            ArrayList<String> categories = new ArrayList<>();
            try (PreparedStatement categoryStmt = conn.prepareStatement("SELECT DISTINCT Category FROM Product");
                 ResultSet categoryRs = categoryStmt.executeQuery()) {
                while (categoryRs.next()) {
                    categories.add(categoryRs.getString("Category"));
                }
            }
            request.setAttribute("categories", categories);
        } catch (Exception e) {
            e.printStackTrace();
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
        return "SearchProductsServlet handles search, brand, and category filtering for products.";
    }
}
