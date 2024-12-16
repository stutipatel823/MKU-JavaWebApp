package com.mku.business;

import com.mku.helper.ProductInfo;
import com.mku.helper.ProductsXML;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CartAPIService implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(CartAPIService.class.getName());
    String cartService = System.getenv("cartService");
    private final String BASE_CART_URL = "http://"+cartService+"/cart/api/carts/";
    private final Client client;

    public CartAPIService() {
        this.client = ClientBuilder.newClient();
    }

    public ProductsXML fetchCartItems(int userId, String token) throws Exception {
        String url = BASE_CART_URL + userId;

        LOGGER.info("Fetching cart items for userId: " + userId + " from URL: " + url);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .get();

        try {
            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                LOGGER.info("No cart items found for userId: " + userId);
                // Return an empty ProductsXML object with no products inside
                ProductsXML emptyProductsXML = new ProductsXML();
                emptyProductsXML.setProducts(new ArrayList<>());  // Empty list of products
                return emptyProductsXML;
//              return null; // Return empty list if no content

            }

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                String errorMessage = "Failed to fetch cart items. HTTP status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }

            try (InputStream inputStream = response.readEntity(InputStream.class)) {
                JAXBContext context = JAXBContext.newInstance(ProductsXML.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                ProductsXML productsXML = (ProductsXML) unmarshaller.unmarshal(inputStream);
                LOGGER.info("Successfully fetched cart items for userId: " + userId);
                return productsXML;
//                return productsXML.getProducts();
            } catch (JAXBException e) {
                LOGGER.log(Level.SEVERE, "Error parsing cart data for userId: " + userId, e);
                throw new Exception("Error parsing cart data.", e);
            }
        } finally {
            response.close();
        }
    }

    public void deleteCartItem(int userId, String productId, String token) throws Exception {
        String url = BASE_CART_URL + "remove/" + userId + "?cartId=" + userId + "&productId=" + productId;

        LOGGER.info("Attempting to delete item with productId: " + productId + " for userId: " + userId);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .delete();

        try {
            int statusCode = response.getStatus();
            if (statusCode == Response.Status.NO_CONTENT.getStatusCode() || statusCode == Response.Status.OK.getStatusCode()) {
                LOGGER.info("Successfully deleted item with productId: " + productId + " for userId: " + userId);
            } else {
                String errorMessage = "Failed to delete item. HTTP status: " + statusCode;
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }
        } finally {
            response.close();
        }
    }
    public void addCartItem(int userId, String productId, String token) throws Exception {
        String url = BASE_CART_URL + "add/" + userId + "?cartId=" + userId + "&productId=" + productId;

        LOGGER.info("Adding item with productId: " + productId + " to cart for userId: " + userId);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .post(null); // POST request with no body

        try {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                LOGGER.info("Successfully added item with productId: " + productId + " to cart for userId: " + userId);
            } else {
                String errorMessage = "Failed to add item to cart. HTTP status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }
        } finally {
            response.close();
        }
    }
    
    public void createUserCart(int userId, String token) throws Exception {
        String url = BASE_CART_URL + "create/" + userId ;

        LOGGER.info("Creating cart with userId: " + userId );

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .post(null); // POST request with no body

        try {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                LOGGER.info("Successfully created cart with cartId: " + userId + " to cart for userId: " + userId);
            } else {
                String errorMessage = "Failed to add item to cart. HTTP status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }
        } finally {
            response.close();
        }
    }
    public void emptyUserCart(int userId, String productId, String token) throws Exception {
        String url = BASE_CART_URL + "empty/" + userId + "?cartId=" + userId;

        LOGGER.info("Attempting to empty cart for userId: " + userId);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .delete();

        try {
            int statusCode = response.getStatus();
            if (statusCode == Response.Status.NO_CONTENT.getStatusCode() || statusCode == Response.Status.OK.getStatusCode()) {
                LOGGER.info("Successfully emptied cart for userId: " + userId);
            } else {
                String errorMessage = "Failed to empty cart. HTTP status: " + statusCode;
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }
        } finally {
            response.close();
        }
    }


    @Override
    public void close() {
        if (client != null) {
            client.close();
            LOGGER.info("CartAPIService client closed.");
        }
    }
}
