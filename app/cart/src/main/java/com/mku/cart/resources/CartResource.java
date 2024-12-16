package com.mku.cart.resources;

import com.mku.cart.business.CartService;
import com.mku.cart.helper.ProductsXML;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/carts")
public class CartResource {

    private static final Logger LOGGER = Logger.getLogger(CartResource.class.getName());
    private final CartService cartService = new CartService();

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getCartItems(@PathParam("userId") int userId) {
        try {
            ProductsXML products = cartService.getCartItemsByUserId(userId);

            // Check if the products list is empty or if it has no items
            if (products.getProducts() == null || products.getProducts().isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(products).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving cart for user " + userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving cart.").build();
        }
    }


    @POST
    @Path("/add/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response addProductToUserCart(@PathParam("userId") int userId,
                                         @QueryParam("cartId") int cartId,
                                         @QueryParam("productId") int productId) {
        //TEMP: cartId is same as userId
        cartId=userId;
        try {
            cartService.addProductToCart(userId, cartId, productId);
            return Response.ok("Product added to cart id: "+cartId).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding product to cart.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error adding product to cart.").build();
        }
    }

    @PUT
    @Path("/update/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateProductQuantity(@PathParam("userId") int userId,
                                          @QueryParam("cartId") int cartId,
                                          @QueryParam("productId") int productId,
                                          @QueryParam("quantity") int quantity) {
         //TEMP: cartId is same as userId
        cartId=userId;
        try {
            cartService.updateProductQuantity(userId, cartId, productId, quantity);
            return Response.ok("Product quantity updated.").build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product quantity.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error updating product quantity.").build();
        }
    }

    @DELETE
    @Path("/remove/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeProductFromCart(@PathParam("userId") int userId,
                                          @QueryParam("cartId") int cartId,
                                          @QueryParam("productId") int productId) {
         //TEMP: cartId is same as userId
        cartId=userId;
        try {
            cartService.removeProductFromCart(userId, cartId, productId);
            return Response.ok("Product removed from cart id: "+cartId).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing product from cart.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error removing product from cart.").build();
        }
    }

    @DELETE
    @Path("/empty/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response emptyCart(@PathParam("userId") int userId,
                              @QueryParam("cartId") int cartId) {
        // TEMP: Assuming cartId is same as userId
        cartId = userId;

        try {
            cartService.emptyCart(userId, cartId);
            return Response.ok("Cart emptied successfully for cart ID: " + cartId).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error emptying cart for user ID: " + userId, e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error emptying cart.").build();
        }
    }

    

    @POST
    @Path("/create/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createCart(@PathParam("userId") int userId) {
        try {
            int cartId = cartService.createCart(userId);

            if (cartId == -1) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Cart already exists for userId: " + userId)
                        .build();
            }
            return Response.ok("Cart created successfully. Cart ID: " + cartId).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating cart.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating cart.")
                    .build();
        }
    }

}
