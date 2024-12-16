package com.mku.business;

import com.mku.helper.OrderInfo;
import com.mku.helper.OrdersXML;
import com.mku.helper.ProductInfo;
import com.mku.helper.ProductsXML;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class OrderAPIService implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(OrderAPIService.class.getName());
    String orderService = System.getenv("orderService");
    private final String BASE_ORDER_URL = "http://"+orderService+"/api/orders/";
    private final Client client;

    public OrderAPIService() {
        this.client = ClientBuilder.newClient();
    }

    public OrdersXML fetchOrders(int userId, String token) throws Exception {
        String url = BASE_ORDER_URL + userId;

        LOGGER.info("Fetching orders for userId: " + userId + " from URL: " + url);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .get();

        try {
            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                LOGGER.info("No orders found for userId: " + userId);
                return new OrdersXML(); // Return empty list if no content
            }

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                String errorMessage = "Failed to fetch orders. HTTP status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }

            try (InputStream inputStream = response.readEntity(InputStream.class)) {
                JAXBContext context = JAXBContext.newInstance(OrdersXML.class, OrderInfo.class, ProductsXML.class, ProductInfo.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                OrdersXML ordersXML = (OrdersXML) unmarshaller.unmarshal(inputStream);
                LOGGER.info("Successfully fetched orders for userId: " + userId);
                return ordersXML;
            } catch (JAXBException e) {
                LOGGER.log(Level.SEVERE, "Error parsing order data for userId: " + userId, e);
                throw new Exception("Error parsing order data.", e);
            }
        } finally {
            response.close();
        }
    }
    public void placeOrder(int userId, int cartId, int paymentId, ProductsXML cartProducts, String token) throws Exception {
        String url = BASE_ORDER_URL + "add/" + userId + "?cartId=" + cartId + "&paymentId=" + paymentId;

        LOGGER.info("Placing order for userId: " + userId + " with cartId: " + cartId + " and paymentId: " + paymentId);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)  // Include the Authorization header with the Bearer token
                .post(javax.ws.rs.client.Entity.xml(cartProducts));  // Send ProductsXML as the XML payload

        try {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                LOGGER.info("Order placed successfully for userId: " + userId);
            } else if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                String errorMessage = "Failed to place order. " + response.readEntity(String.class);
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            } else if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                String errorMessage = "Server error while placing order. " + response.readEntity(String.class);
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            } else {
                String errorMessage = "Unexpected response status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }
        } finally {
            response.close();
        }
    }
    
    @Override
    public void close() {  // Override close method from AutoCloseable
        if (client != null) {
            client.close();
            LOGGER.info("OrderAPIService client closed.");
        }
    }

}
