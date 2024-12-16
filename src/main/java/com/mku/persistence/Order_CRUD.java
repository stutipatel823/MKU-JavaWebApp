package com.mku.persistence;

import com.mku.helper.OrderInfo;
import com.mku.helper.ProductInfo;
import com.mku.helper.ProductsXML;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Order_CRUD {
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

    /**
     * Find all orders for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of OrderInfo objects.
     * @throws SQLException if an error occurs during the database operation.
     */
    public static List<OrderInfo> getOrdersByUserId(int userId) throws SQLException {
        List<OrderInfo> orders = new ArrayList<>();
        String orderQuery = "SELECT * FROM `Order` WHERE user_id = ? ORDER BY order_date DESC";
        String productQuery = "SELECT p.product_id, p.name, p.price, op.quantity " +
                              "FROM Order_Product op " +
                              "JOIN Product p ON op.product_id = p.product_id " +
                              "WHERE op.order_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderQuery)) {
            orderStmt.setInt(1, userId);

            try (ResultSet orderRs = orderStmt.executeQuery()) {
                while (orderRs.next()) {
                    OrderInfo order = new OrderInfo(
                        orderRs.getInt("order_id"),
                        orderRs.getString("order_date"),
                        orderRs.getDouble("amount"),
                        orderRs.getString("transaction_date"),
                        orderRs.getString("status"),
                        userId,
                        orderRs.getInt("cart_id"),
                        orderRs.getInt("payment_id"),
                        new ProductsXML()
                    );

                    List<ProductInfo> productsInOrder = new ArrayList<>();
                    try (PreparedStatement productStmt = conn.prepareStatement(productQuery)) {
                        productStmt.setInt(1, order.getOrderId());
                        try (ResultSet productRs = productStmt.executeQuery()) {
                            while (productRs.next()) {
                                ProductInfo product = new ProductInfo();
                                product.setProductId(productRs.getInt("product_id"));
                                product.setName(productRs.getString("name"));
                                product.setPrice(productRs.getDouble("price"));
                                product.setQuantity(productRs.getInt("quantity"));
                                productsInOrder.add(product);
                            }
                        }
                    }

                    ProductsXML productsXML = new ProductsXML();
                    productsXML.setProducts(new ArrayList<>(productsInOrder));
                    order.setProductsOrdered(productsXML);
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    /**
     * Insert a new order into the database.
     *
     * @param userId      the ID of the user.
     * @param cartId      the ID of the cart associated with the order.
     * @param paymentId   the ID of the payment associated with the order.
     * @param totalAmount the total amount of the order.
     * @param cartProducts a list of ProductInfo objects.
     * @return the ID of the newly created order.
     * @throws SQLException if an error occurs during the database operation.
     */
    public static int insertOrder(int userId, int cartId, int paymentId, double totalAmount, List<ProductInfo> cartProducts) throws SQLException {
        String checkPaymentIdQuery = "SELECT payment_id FROM Payment WHERE user_id = ?";
        String insertOrderQuery = "INSERT INTO `Order` (order_date, amount, transaction_date, status, user_id, cart_id, payment_id) VALUES (NOW(), ?, NOW(), ?, ?, ?, ?)";
        String insertOrderProductQuery = "INSERT INTO Order_Product (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement paymentStmt = conn.prepareStatement(checkPaymentIdQuery);
             PreparedStatement orderStmt = conn.prepareStatement(insertOrderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement productStmt = conn.prepareStatement(insertOrderProductQuery)) {

            String status = "Pending";
            paymentStmt.setInt(1, paymentId);
            try (ResultSet rs = paymentStmt.executeQuery()) {
                if (rs.next()) {
                    status = "Shipped";
                }
            }

            orderStmt.setDouble(1, totalAmount);
            orderStmt.setString(2, status);
            orderStmt.setInt(3, userId);
            orderStmt.setInt(4, cartId);
            orderStmt.setInt(5, paymentId);
            orderStmt.executeUpdate();

            int orderId;
            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve order ID.");
                }
            }

            for (ProductInfo product : cartProducts) {
                productStmt.setInt(1, orderId);
                productStmt.setInt(2, product.getProductId());
                productStmt.setInt(3, product.getQuantity());
                productStmt.executeUpdate();
            }

            return orderId;
        }
    }
    

    

    public static void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        // Escape the reserved keyword "Order" with backticks.
        String updateStatusQuery = "UPDATE `Order` SET status = ? WHERE order_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement updateStatusStmt = conn.prepareStatement(updateStatusQuery)) {

            updateStatusStmt.setString(1, newStatus);
            updateStatusStmt.setInt(2, orderId);
            updateStatusStmt.executeUpdate();
        }
    }

    public static void deleteOrder(int orderId) throws SQLException {
        String deleteOrderQuery = "DELETE FROM `Order` WHERE order_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement deleteOrderStmt = conn.prepareStatement(deleteOrderQuery)){

            // Delete the order itself (products get deleted from Order_Product Schema due to cascade in sql command)
            deleteOrderStmt.setInt(1, orderId);
            deleteOrderStmt.executeUpdate();
        }
    }
    
    public static List<OrderInfo> getAllOrders() throws SQLException {
        List<OrderInfo> orders = new ArrayList<>();
        // Escape the reserved keyword "Order" with backticks.
        String orderQuery = "SELECT * FROM `Order` ORDER BY order_date DESC";
        String productQuery = "SELECT p.product_id, p.name, p.price, op.quantity " +
                              "FROM Order_Product op " +
                              "JOIN Product p ON op.product_id = p.product_id " +
                              "WHERE op.order_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderQuery)) {

            try (ResultSet orderRs = orderStmt.executeQuery()) {
                while (orderRs.next()) {
                    OrderInfo order = new OrderInfo(
                        orderRs.getInt("order_id"),
                        orderRs.getString("order_date"),
                        orderRs.getDouble("amount"),
                        orderRs.getString("transaction_date"),
                        orderRs.getString("status"),
                        orderRs.getInt("user_id"),
                        orderRs.getInt("cart_id"),
                        orderRs.getInt("payment_id"),
                        new ProductsXML()
                    );

                    List<ProductInfo> productsInOrder = new ArrayList<>();
                    try (PreparedStatement productStmt = conn.prepareStatement(productQuery)) {
                        productStmt.setInt(1, order.getOrderId());
                        try (ResultSet productRs = productStmt.executeQuery()) {
                            while (productRs.next()) {
                                ProductInfo product = new ProductInfo();
                                product.setProductId(productRs.getInt("product_id"));
                                product.setName(productRs.getString("name"));
                                product.setPrice(productRs.getDouble("price"));
                                product.setQuantity(productRs.getInt("quantity"));
                                productsInOrder.add(product);
                            }
                        }
                    }

                    ProductsXML productsXML = new ProductsXML();
                    productsXML.setProducts(new ArrayList<>(productsInOrder));
                    order.setProductsOrdered(productsXML);
                    orders.add(order);
                }
            }
        }
        return orders;
    }


}
