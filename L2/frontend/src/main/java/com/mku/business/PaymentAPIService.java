package com.mku.business;

import com.mku.helper.PaymentInfo;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

public class PaymentAPIService implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(PaymentAPIService.class.getName());
    String paymentService = System.getenv("paymentService");
    private final String BASE_PAYMENT_URL = "http://"+paymentService+"/payment/api/payments/";  // Backend service URL
    private final Client client;

    public PaymentAPIService() {
        this.client = ClientBuilder.newClient();
    }

   
    public PaymentInfo fetchPaymentMethod(int userId, String token) throws Exception {
        String url = BASE_PAYMENT_URL + userId;  // Building the URL to access the user's payment method
        LOGGER.info("Fetching payment method for userId: " + userId + " from URL: " + url);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)  // Bearer token for authentication
                .get();

        try {
            if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                LOGGER.info("No payment method found for userId: " + userId);
                return null;  // Return empty PaymentInfo if not found
            }

            // Handling 500 Internal Server Error, and returning null instead of throwing an exception
            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                LOGGER.warning("Server error occurred while fetching payment method for userId: " + userId);
                return null;  // Return null for server error
            }

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                LOGGER.warning("Unexpected status code received: " + response.getStatus());
                return null;  // In case of other non-OK statuses, return null
            }


            return response.readEntity(PaymentInfo.class);  // Directly returning the PaymentInfo object from the response
        } finally {
            response.close();
        }
    }

    
    
    public void updatePaymentMethod(int userId, String token, String paymentType, String cardNumber, String expDate, String paypalEmail) throws Exception {
        String url = BASE_PAYMENT_URL + "update/" + userId;  // Backend update endpoint
        LOGGER.info("Updating payment method for userId: " + userId + " at URL: " + url);

        // Construct the form data using the Form class
        Form formData = new Form()
                .param("paymentType", paymentType)
                .param("cardNumber", cardNumber)
                .param("expDate", expDate)
                .param("paypalEmail", paypalEmail);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .put(Entity.form(formData));  // PUT request to update payment with a Form entity

        try {
            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                LOGGER.info("No content returned for update, possibly no changes made.");
            }

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                String errorMessage = "Failed to update payment method. HTTP status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }
        } finally {
            response.close();
        }
    }
    public void addPaymentMethod(int userId, String token, String paymentType, String cardNumber, String expDate, String paypalEmail) throws Exception {
        String url = BASE_PAYMENT_URL + "add/" + userId;  // Backend add endpoint
        LOGGER.info("Adding payment method for userId: " + userId + " at URL: " + url);

        // Construct the form data using the Form class
        Form formData = new Form()
                .param("paymentType", paymentType)
                .param("cardNumber", cardNumber)
                .param("expDate", expDate)
                .param("paypalEmail", paypalEmail);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .post(Entity.form(formData));  // POST request to add payment with a Form entity

        try {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                String errorMessage = "Failed to add payment method. HTTP status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }

            LOGGER.info("Payment method added successfully for userId: " + userId);
        } finally {
            response.close();
        }
    }

    
    
    

    @Override
    public void close() {
        if (client != null) {
            client.close();
            LOGGER.info("PaymentAPIService client closed.");
        }
    }
}
