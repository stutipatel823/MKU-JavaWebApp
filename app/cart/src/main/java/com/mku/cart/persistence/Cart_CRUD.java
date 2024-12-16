package com.mku.cart.persistence;

import com.mku.cart.helper.ProductInfo;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Cart_CRUD {
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/Cart_MKU";
    private static final String DB_USERNAME = "root"; 
    private static final String DB_PASSWORD = "root1234"; 

//    private static Connection getConnection() throws SQLException {
//        String connection = System.getenv("DB_URL");
//        if (connection == null) {
//            throw new IllegalStateException("DB_URL environment variable is not set");
//        }
//        String DB_URL = "jdbc:mysql://" + connection + "/Cart_MKU";
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new SQLException("MySQL Driver not found", e);
//        }
//        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
//    }
    private static Connection getConnection() throws SQLException {
        // Ensure both DB_URL and DB_PORT are set in environment variables
        String dbUrl = System.getenv("DB_URL");
        String dbPort = System.getenv("DB_PORT");
        if (dbUrl == null || dbUrl.isEmpty() || dbPort == null || dbPort.isEmpty()) {
            throw new IllegalStateException("DB_URL or DB_PORT environment variable is not set");
        }

        // Construct the connection string properly
        String DB_URL = "jdbc:mysql://" + dbUrl + ":" + dbPort + "/Cart_MKU";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }

        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    public static int createCart(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        String checkQuery = "SELECT cart_id FROM Cart WHERE user_id = ?";
        String insertQuery = "INSERT INTO Cart (user_id, created_at, updated_at) VALUES (?, NOW(), NOW())";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            // Check if the cart already exists
            checkStmt.setInt(1, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Cart already exists
                    return -1;
                }
            }

            // Create a new cart if it doesn't exist
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, userId);
                int rowsAffected = insertStmt.executeUpdate();

                if (rowsAffected == 1) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Return the new cart ID
                        }
                    }
                }
                throw new SQLException("Failed to create a new cart.");
            }
        }
    }

    public static Map<Integer, ProductInfo> getCartItemsByUserId(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        Map<Integer, ProductInfo> products = new HashMap<>();
        String query = "SELECT p.product_id, p.name, p.price, cp.quantity, p.brand, p.imageURL, p.stock, p.availability " +
                       "FROM Product p " +
                       "JOIN Cart_Product cp ON p.product_id = cp.product_id " +
                       "JOIN Cart c ON cp.cart_id = c.cart_id WHERE c.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ProductInfo product = new ProductInfo();
                    product.setProductId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setBrand(rs.getString("brand"));
                    product.setPrice(rs.getDouble("price") * rs.getInt("quantity"));
                    product.setImageURL(rs.getString("imageURL"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setStock(rs.getInt("stock"));
                    product.setAvailability(rs.getBoolean("availability"));
                    products.put(product.getProductId(), product);
                }
            }
        }
        return products;
    }

    public static void updateProductQuantity(int cartId, int productId, int quantity) throws SQLException {
        if (cartId <= 0 || productId <= 0 || quantity <= 0) {
            throw new IllegalArgumentException("Invalid input values.");
        }

        String stockQuery = "SELECT stock FROM Product WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stockPst = conn.prepareStatement(stockQuery)) {
            stockPst.setInt(1, productId);
            try (ResultSet rs = stockPst.executeQuery()) {
                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    if (quantity > stock) {
                        throw new SQLException("Requested quantity exceeds stock.");
                    }
                } else {
                    throw new SQLException("Product not found.");
                }
            }

            String updateQuery = "UPDATE Cart_Product SET quantity = ? WHERE cart_id = ? AND product_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(updateQuery)) {
                pst.setInt(1, quantity);
                pst.setInt(2, cartId);
                pst.setInt(3, productId);
                pst.executeUpdate();  // SQLException will be thrown if no rows are updated
            }
        }
    }

    public static void removeProductFromCart(int cartId, int productId) throws SQLException {
        if (cartId <= 0 || productId <= 0) {
            throw new IllegalArgumentException("Invalid cart ID or product ID.");
        }

        String query = "DELETE FROM Cart_Product WHERE cart_id = ? AND product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, cartId);
            pst.setInt(2, productId);
            pst.executeUpdate();  // SQLException will be thrown if no rows are deleted
        }
    }
    public static void emptyCart(int cartId) throws SQLException {
        if (cartId <= 0) {
            throw new IllegalArgumentException("Invalid cart ID.");
        }

        String query = "DELETE FROM Cart_Product WHERE cart_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, cartId);
            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Cart emptied successfully.");
            } else {
                System.out.println("No products found in the cart.");
            }
        }
    }


    public static void insertProductToCart(int cartId, int productId) throws SQLException {
        if (cartId <= 0 || productId <= 0) {
            throw new IllegalArgumentException("Invalid cart ID or product ID.");
        }

        String upsertQuery = "INSERT INTO Cart_Product (cart_id, product_id, quantity) VALUES (?, ?, 1) " +
                             "ON DUPLICATE KEY UPDATE quantity = quantity + 1";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(upsertQuery)) {
            pst.setInt(1, cartId);
            pst.setInt(2, productId);
            pst.executeUpdate();  // SQLException will be thrown if insertion fails
        }
    }

}
