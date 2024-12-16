package com.mku.search.persistence;

import com.mku.search.helper.ProductInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchProduct_CRUD {
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/Search_MKU"; 
    private static final String DB_USERNAME = "root"; 
    private static final String DB_PASSWORD = "root1234"; 

    private static Connection getConnection() throws SQLException {
        String dbUrl = System.getenv("DB_URL");
        String dbPort = System.getenv("DB_PORT");
        if (dbUrl == null || dbUrl.isEmpty() || dbPort == null || dbPort.isEmpty()) {
            throw new IllegalStateException("DB_URL or DB_PORT environment variable is not set");
        }

        String DB_URL = "jdbc:mysql://" + dbUrl +":"+ dbPort + "/Search_MKU";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
    
    public static ArrayList<ProductInfo> allProducts() throws SQLException{
        ArrayList<ProductInfo> products = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement pst = conn.prepareStatement("SELECT * FROM Product");
                ResultSet rs = pst.executeQuery()) {
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
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static ArrayList<ProductInfo> searchForProducts(String searchInput, String brandSelect, String categorySelect) {
        ArrayList<ProductInfo> products = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Product WHERE 1=1");
        boolean isSearchOrFilterApplied = false;

        if (searchInput != null && !searchInput.isEmpty()) {
            query.append(" AND name LIKE ?");
            isSearchOrFilterApplied = true;
        }

        if (brandSelect != null && !brandSelect.isEmpty() && !brandSelect.equals("*")) {
            query.append(" AND brand = ?");
            isSearchOrFilterApplied = true;
        }

        if (categorySelect != null && !categorySelect.isEmpty() && !categorySelect.equals("*")) {
            query.append(" AND category = ?");
            isSearchOrFilterApplied = true;
        }

        // If no search or filter is applied, retrieve all products
        if (!isSearchOrFilterApplied) {
            query = new StringBuilder("SELECT * FROM Product");
        }

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;

            if (searchInput != null && !searchInput.isEmpty()) {
                pst.setString(paramIndex++, "%" + searchInput + "%");
            }

            if (brandSelect != null && !brandSelect.isEmpty() && !brandSelect.equals("*")) {
                pst.setString(paramIndex++, brandSelect);
            }

            if (categorySelect != null && !categorySelect.isEmpty() && !categorySelect.equals("*")) {
                pst.setString(paramIndex++, categorySelect);
            }

            ResultSet rs = pst.executeQuery();

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return products;
    }

    // Method to retrieve distinct brands and categories for filtering
    public static void setBrandAndCategoryAttributes(ArrayList<String> brands, ArrayList<String> categories) {
        try {
            // Get distinct brands
            try (Connection conn = getConnection();
                 PreparedStatement brandStmt = conn.prepareStatement("SELECT DISTINCT brand FROM Product");
                 ResultSet brandRs = brandStmt.executeQuery()) {
                while (brandRs.next()) {
                    brands.add(brandRs.getString("brand"));
                }
            }

            // Get distinct categories
            try (Connection conn = getConnection();
                 PreparedStatement categoryStmt = conn.prepareStatement("SELECT DISTINCT category FROM Product");
                 ResultSet categoryRs = categoryStmt.executeQuery()) {
                while (categoryRs.next()) {
                    categories.add(categoryRs.getString("category"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
