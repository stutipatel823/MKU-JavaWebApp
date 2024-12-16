package com.mku.order.business;

import com.mku.order.helper.OrderInfo;
import com.mku.order.helper.OrdersXML;
import com.mku.order.helper.PaymentInfo;
import com.mku.order.helper.ProductInfo;
import com.mku.order.helper.ProductsXML;
import com.mku.order.persistence.Order_CRUD;
import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
/**
 * Service layer for Order operations.
 */
public class OrderService {
    /**
     * Retrieve all orders for a user.
     *
     * @param userId the ID of the user.
     * @return an OrdersXML object containing the user's orders.
     * @throws SQLException if an error occurs during the database query.
     */
    public OrdersXML getOrdersByUserId(int userId) throws SQLException {
        List<OrderInfo> orders = Order_CRUD.getOrdersByUserId(userId);

        if (orders == null || orders.isEmpty()) {
            return null; // Return null if no orders are found
        }

        OrdersXML ordersXML = new OrdersXML();
        ordersXML.setOrders(new ArrayList<>(orders)); // Convert List to ArrayList if needed
        return ordersXML;
    }


    /**
     * Place a new order for the user.
     *
     * @param userId      the ID of the user.
     * @param cartId      the ID of the cart associated with the order.
     * @param paymentId   the ID of the payment associated with the order.
     * @param cartProducts a ProductsXML object containing the cart's products.
     * @return the ID of the newly created order.
     * @throws SQLException if an error occurs during the database operation.
     */
    
//    public int createOrder(int userId, int cartId, int paymentId, ProductsXML cartProducts) throws SQLException, IOException {
//        // Validate cart products (e.g., check for null or empty cart)
//        if (cartProducts == null || cartProducts.getProducts().isEmpty()) {
//            throw new IllegalArgumentException("Cart is empty or invalid. Cannot place order for user: " + userId);
//        }
//
//        // Calculate the total amount for the order
//        double totalAmount = 0.0;
//        for (ProductInfo product : cartProducts.getProducts()) {
//            totalAmount += product.getPrice() * product.getQuantity();
//        }
//
//        // Create an instance of OrderMessaging for the order-specific data
//        OrderMessaging orderMessaging = new OrderMessaging(cartProducts, totalAmount);
//
//        // Initiate the order
//        orderMessaging.initiateOrder(userId);
//        int orderId = orderMessaging.getOrderId();
//        
//        // If order insertion fails, return -1
//        if (orderId == -1) {
//            throw new SQLException("Failed to place order for user: " + userId);
//        }
//
//        // Return the order ID if everything succeeds
//        return orderId;
//    }
    public int createOrder(int userId, int cartId, int paymentId, ProductsXML cartProducts) throws SQLException, Exception {
        // Validate paymentId by calling the payment service
//        try {
//            PaymentInfo paymentInfo = paymentAPICall(paymentId);
//
//            // If payment validation fails (non-200 response), return -2
//            if (paymentInfo == null) {
//                return -2;  // Payment validation failed
//            }
//        } catch (Exception e) {
//            // Handle payment failure (non-200 response)
//            System.err.println("Payment validation failed: " + e.getMessage());
//            return -2;  // Payment validation failed
//        }

        // Validate cart products (e.g., check for null or empty cart)
        if (cartProducts == null || cartProducts.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or invalid. Cannot place order for user: " + userId);
        }

        // Calculate the total amount for the order
        double totalAmount = 0.0;
        for (ProductInfo product : cartProducts.getProducts()) {
            totalAmount += product.getPrice() * product.getQuantity();
        }

        // Attempt to place the order and insert into the database
        int orderId = Order_CRUD.insertOrder(userId, cartId, paymentId, totalAmount, cartProducts.getProducts());

        // If order insertion fails, return -1
        if (orderId == -1) {
            throw new SQLException("Failed to place order for user: " + userId);
        }

        // Return the order ID if everything succeeds
        return orderId;
    }


    /**
     * Retrieve all orders from the database.
     *
     * @return OrdersXML containing all orders.
     * @throws SQLException if any error occurs during the database operation.
     */
    public OrdersXML getAllOrders() throws SQLException {
        List<OrderInfo> orders = Order_CRUD.getAllOrders();

        OrdersXML ordersXML = new OrdersXML();
        ordersXML.setOrders(new ArrayList<>(orders));
        return ordersXML;
    }

    /**
     * Delete an order by its ID.
     *
     * @param orderId the ID of the order to delete.
     * @throws SQLException if any error occurs during the database operation.
     */
    public void deleteOrder(int orderId) throws SQLException {
        Order_CRUD.deleteOrder(orderId);
    }

    /**
     * Update the status of an order.
     *
     * @param orderId the ID of the order.
     * @param newStatus the new status to set for the order.
     * @throws SQLException if any error occurs during the database operation.
     */
    public void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        Order_CRUD.updateOrderStatus(orderId, newStatus);
    }
    
    
    private PaymentInfo paymentAPICall(int paymentId) throws Exception {
        String paymentService = System.getenv("paymentService");
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://"+paymentService+"/api/payments/" + paymentId);
        
        Response response = target.request(MediaType.APPLICATION_XML).get();
        
        // If payment service response is not 200 (OK), return null or throw an exception
        if (response.getStatus() != 200) {
            return null;  // Return null to indicate payment failure
        }
        
        return response.readEntity(PaymentInfo.class);
    }
}


