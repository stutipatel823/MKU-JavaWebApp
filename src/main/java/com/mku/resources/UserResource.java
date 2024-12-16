package com.mku.resources;

import com.mku.business.UserService;
import com.mku.helper.UserInfo;
import java.sql.SQLException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST Web Service for User Operations
 */
@Path("/users")
public class UserResource {

    private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());
    private final UserService userService = new UserService();

    /**
     * Endpoint for registering a new user.
     * 
     * @param <error>
     * @param userInfo the user details.
     * @param confirmPassword the password confirmation for validation.
     * @return Response indicating whether the registration was successful or failed.
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerUser(@FormParam("firstname") String firstname,
                                 @FormParam("lastname") String lastname,
                                 @FormParam("email") String email,
                                 @FormParam("password") String password,
                                 @FormParam("confirmPassword") String confirmPassword) {
        try {
            userService.registerUser(firstname, lastname, email, password, confirmPassword);
            return Response.status(Response.Status.CREATED)
                           .entity("User registered successfully.")
                           .build();
        } catch (IllegalArgumentException e) {
            // Catch validation issues
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Validation error: " + e.getMessage())
                           .build();
        } catch (SQLException e) {
            // Catch SQL-related issues
            LOGGER.log(Level.SEVERE, "Database error during registration", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("An internal error occurred. Please try again later.")
                           .build();
        }
    }

    /**
     * Endpoint for authenticating a user with their credentials.
     * 
     * @param email The user's email.
     * @param password The user's password.
     * @return XML representation of the user if authentication is successful, otherwise an error message in plain text.
     */
    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // Accept form parameters (email & password)
    @Produces(MediaType.APPLICATION_XML)  // Respond with XML for successful authentication
    public Response authenticateUser(@QueryParam("email") String email, @QueryParam("password") String password) {
        try {
            // Call the service to authenticate the user
            UserInfo user = userService.authenticateUser(email, password);

            if (user != null) {
                return Response.status(Response.Status.OK)
                               .entity(user)  // Return UserInfo as XML response (JAXB does the conversion)
                               .build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                               .entity("Authentication failed. Invalid email or password.")  // Plain text error response
                               .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during authentication", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error during authentication: " + e.getMessage())  // Plain text error response
                           .build();
        }
    }
}
