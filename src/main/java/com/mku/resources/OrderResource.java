package com.mku.resources;

import com.mku.business.OrderService;
import com.mku.helper.OrdersXML;
import com.mku.helper.ProductsXML;
import java.io.StringWriter;
import java.sql.SQLException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

/**
 * REST Web Service for Order Operations
 */
@Path("/orders")
public class OrderResource {

    private static final Logger LOGGER = Logger.getLogger(OrderResource.class.getName());
    private final OrderService orderService = new OrderService();

    /**
     * Retrieve all orders for all users.
     *
     * @return XML representation of all orders.
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllOrders() {
        try {
            OrdersXML orders = orderService.getAllOrders();
            return Response.ok(orders).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all orders", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving orders.").build();
        }
    }

    /**
     * Retrieve all orders for a specific user.
     *
     * @param userId the ID of the user whose orders are being retrieved.
     * @return XML representation of the user's orders.
     */
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getOrdersByUserId(@PathParam("userId") int userId) {
        try {
            OrdersXML orders = orderService.getOrdersByUserId(userId);
            if (orders == null || orders.getOrders().isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No orders found for user: " + userId).build();
            }
            return Response.ok(orders).build(); // Return OrdersXML as XML response
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving orders for user " + userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving orders.").build();
        }
    }

    /**
     * Place an order for the user.
     *
     * @param userId      the ID of the user.
     * @param cartId      the ID of the cart.
     * @param paymentId   the ID of the payment.
     * @param cartProducts a ProductsXML object representing the cart's products.
     * @return response with a success message in plain text.
     */
    @POST
    @Path("/add/{userId}")
    @Consumes(MediaType.APPLICATION_XML)  // Expecting XML request payload for ProductsXML
    @Produces(MediaType.TEXT_PLAIN)  // Return the response as plain text
    public Response createOrder(@PathParam("userId") int userId,
                               @QueryParam("cartId") int cartId,
                               @QueryParam("paymentId") int paymentId,
                               ProductsXML cartProducts) throws SQLException, PropertyException, JAXBException {

        try {
            // Call the service to place the order and get the order ID (int)
            int orderId = orderService.createOrder(userId, cartId, paymentId, cartProducts);

            // Construct the success message
            String successMessage = "Order placed successfully. Order ID: " + orderId;
            // Return success message as plain text
            return Response.status(Response.Status.CREATED).entity(successMessage).build();
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid input while placing order for user " + userId, e);
            String errorMessage = e.getMessage();
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error placing order for user " + userId, e);
            String errorMessage = "Error placing order.";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
        }
    }

    /**
     * Delete a specific order.
     *
     * @param orderId the ID of the order to delete.
     * @return response with a success message in plain text.
     */
    @DELETE
    @Path("/delete/{orderId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteOrder(@PathParam("orderId") int orderId) {
        try {
            orderService.deleteOrder(orderId);
            return Response.status(Response.Status.NO_CONTENT).entity("Order deleted successfully.").build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting order with ID " + orderId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting order.").build();
        }
    }

    /**
     * Update the status of a specific order.
     *
     * @param orderId the ID of the order.
     * @param status the new status of the order.
     * @return response with a success message in plain text.
     */
    @PUT
    @Path("/update/{orderId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateOrderStatus(@PathParam("orderId") int orderId, @QueryParam("status") String newStatus) {
        try {
            orderService.updateOrderStatus(orderId, newStatus);
            return Response.status(Response.Status.OK)
                    .entity("Order status updated successfully.")
                    .build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating order status for order ID " + orderId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating order status.")
                    .build();
        }
    }
}
