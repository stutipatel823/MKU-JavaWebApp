package com.mku.resources;

import com.mku.business.PaymentService;
import com.mku.helper.PaymentInfo;
import com.mku.helper.PaymentsXML;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBException;

/**
 * REST Web Service for Payment
 */
@Path("payments")
public class PaymentResource {
    private static final Logger LOGGER = Logger.getLogger(PaymentResource.class.getName());
    private final PaymentService paymentService = new PaymentService();

   /**
    * Retrieves all payment methods from the database and returns them in XML format.
    * If an error occurs, an appropriate error message is returned.
    * @return A Response containing the payment methods in XML or an error message.
    */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllPaymentMethods() {
        try {
            // Get the XML representation of the payment methods list
            PaymentsXML paymentInfoList = paymentService.getAllPaymentMethods();
            
            // Return the PaymentsXML object which is JAXB annotated and will be converted to XML
            return Response.ok(paymentInfoList).build();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all payment methods.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve payment methods due to a database error.")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unexpected error occurred.")
                    .build();
        }
    }
    
    
    /**
     * Retrieve the payment information for a user.
     * @param userId the ID of the user whose payment method is being retrieved.
     * @return XML representation of the user's payment information.
     */
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getPaymentMethod(@PathParam("userId") int userId) {
        try {
            PaymentInfo paymentInfo = paymentService.getPaymentMethodByUserId(userId);
            if (paymentInfo == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Payment Method not found for user " + userId).build();
            }
            return Response.ok(paymentInfo).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving payment method for user " + userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving payment method.").build();
        }
    }

    /**
     * Add a new payment method for the user.
     * @param userId the ID of the user.
     * @param paymentType the type of payment (e.g., "CreditCard", "PayPal").
     * @param cardNumber the card number (if applicable).
     * @param expDate the expiration date (if applicable).
     * @param paypalEmail the PayPal email (if applicable).
     * @return A success message if payment is created successfully.
     */
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addPayment(
        @QueryParam("userId") int userId,
        @FormParam("paymentType") String paymentType,
        @FormParam("cardNumber") String cardNumber,
        @FormParam("expDate") String expDate,
        @FormParam("paypalEmail") String paypalEmail
    ) {
        try {
            int paymentId = paymentService.createPaymentMethodForUser(userId, paymentType, cardNumber, expDate, paypalEmail);

            return Response.status(Response.Status.CREATED)
                    .entity("Payment method added successfully. Payment ID: " + paymentId)
                    .build();
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "User already has a payment method: " + userId, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User already has a payment method. Use updatePayment instead.")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding payment method for user " + userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to add payment method.")
                    .build();
        }
    }

    /**
     * Update an existing payment method for the user.
     * @param userId the ID of the user.
     * @param paymentType the type of payment (e.g., "CreditCard", "PayPal").
     * @param cardNumber the card number (if applicable).
     * @param expDate the expiration date (if applicable).
     * @param paypalEmail the PayPal email (if applicable).
     * @return A success message if the payment method is updated successfully.
     */
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePayment(
        @QueryParam("userId") int userId,
        @FormParam("paymentType") String paymentType,
        @FormParam("cardNumber") String cardNumber,
        @FormParam("expDate") String expDate,
        @FormParam("paypalEmail") String paypalEmail
    ) {
        try {
            PaymentInfo currentPayment = paymentService.getPaymentMethodByUserId(userId);

            if (currentPayment == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No payment method found for user " + userId)
                        .build();
            }

            paymentService.updatePaymentMethodForUser(userId, paymentType, cardNumber, expDate, paypalEmail, true);

            return Response.status(Response.Status.OK)
                    .entity("Payment method updated successfully.")
                    .build();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error updating payment method for user " + userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update payment method due to database error.")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error updating payment method for user " + userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update payment method.")
                    .build();
        }
    }

    /**
     * Delete the payment method for a user.
     * @param paymentId the ID of the payment method to delete.
     * @return A success or error message.
     */
    @DELETE
    @Path("/delete/{paymentId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deletePayment(@PathParam("paymentId") int paymentId) {
        try {
            paymentService.deletePaymentMethodForUser(paymentId);
            return Response.status(Response.Status.OK)
                    .entity("Payment method with ID " + paymentId + " deleted successfully.")
                    .build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting payment method with ID: " + paymentId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete payment method. Please try again later.")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while deleting payment method with ID " + paymentId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete payment method.")
                    .build();
        }
    }

   
}
