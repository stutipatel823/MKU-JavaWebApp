package com.mku.persistence;

import com.mku.helper.ProductInfo;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Cart_CRUD {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku"; 
    private static final String DB_USERNAME = "root"; 
    private static final String DB_PASSWORD = "root1234"; 

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    public static void createCart(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        String query = "INSERT INTO Cart(cart_id, created_at, updated_at, user_id) VALUES (user_id, NOW(), NOW(), ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.executeUpdate();  // SQLException will be thrown if insertion fails
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
