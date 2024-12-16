package com.mku.business;

import com.mku.helper.OrderInfo;
import com.mku.helper.OrdersXML;
import com.mku.helper.PaymentInfo;
import com.mku.helper.ProductInfo;
import com.mku.helper.ProductsXML;
import com.mku.persistence.Order_CRUD;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
            throw new IllegalArgumentException("No orders found for user: " + userId);
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
    public int createOrder(int userId, int cartId, int paymentId, ProductsXML cartProducts) throws SQLException {
        // Validate cart products
        if (cartProducts == null || cartProducts.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or invalid. Cannot place order for user: " + userId);
        }

        // Calculate the total amount for the order
        double totalAmount = 0.0;
        for (ProductInfo product : cartProducts.getProducts()) {
            totalAmount += product.getPrice() * product.getQuantity();
        }

        // Place the order
        int orderId = Order_CRUD.insertOrder(userId, cartId, paymentId, totalAmount, cartProducts.getProducts());

        if (orderId == -1) {
            throw new SQLException("Failed to place order for user: " + userId);
        }

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
    
    private Client client = ClientBuilder.newClient(); // JAX-RS Client
    private String cartApiUrl = "http://localhost:8080/version3/api/carts";  // Cart Microservice API
    private String paymentApiUrl = "http://localhost:8080/version3/api/payments";  // Payment Microservice API

    /**
     * Fetch cart data from the Cart API.
     *
     * @param userId the ID of the user.
     * @return ProductsXML containing the cart data.
     * @throws Exception if the API call fails or the response is invalid.
     */
    public ProductsXML getCartDataByApiUrl(int userId) throws Exception {
        WebTarget target = client.target(cartApiUrl+'/'+userId);

        // Perform the GET request and get the response
        Response response = target.request(MediaType.APPLICATION_XML).get();

        if (response.getStatus() != 200) {
            throw new Exception("Failed to fetch cart data. HTTP Status: " + response.getStatus());
        }

        // Read and return the response as ProductsXML
        return response.readEntity(ProductsXML.class);
    }

    /**
     * Fetch payment data from the Payments API.
     *
     * @param userId the ID of the user.
     * @return PaymentInfo containing the payment data.
     * @throws Exception if the API call fails or the response is invalid.
     */
    public PaymentInfo getPaymentDataByApiUrl(int userId) throws Exception {
        WebTarget target = client.target(paymentApiUrl+'/'+userId);

        // Perform the GET request and get the response
        Response response = target.request(MediaType.APPLICATION_XML).get();

        if (response.getStatus() != 200) {
            throw new Exception("Failed to fetch payment data. HTTP Status: " + response.getStatus());
        }

        // Read and return the response as PaymentInfo
        return response.readEntity(PaymentInfo.class);
    }

    public void create(Integer userId, Integer userId0, Integer userId1, ArrayList<ProductInfo> cartItems) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
