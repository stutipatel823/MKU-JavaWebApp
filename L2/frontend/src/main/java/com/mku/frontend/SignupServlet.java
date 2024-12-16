package com.mku.frontend;

import com.mku.business.UserService;
import com.mku.business.Authenticate;
import com.mku.business.CartAPIService;
import com.mku.business.OrderAPIService;
import com.mku.helper.UserInfo;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SignupServlet", urlPatterns = {"/signup"})
public class SignupServlet extends HttpServlet {
    private final UserService userService = new UserService();  // Business logic instance
    private final Authenticate authenticate = new Authenticate();  // JWT generation instance
    private final String authenticationCookieName = "signup_token"; // Cookie name for signup token

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Display the signup form
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/signup.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Retrieve form parameters
        String firstname = capitalizeFirstLetter(request.getParameter("firstname"));
        String lastname = capitalizeFirstLetter(request.getParameter("lastname"));
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
            dispatcher.forward(request, response);
            return;
        }

        try {
            // Call business logic to register the user and retrieve userId
            int userId = userService.registerUser(firstname, lastname, email, password, confirmPassword);

            // Generate JWT token for the newly registered user
            String jwtToken = authenticate.createJWT("login-token", email, 3600000); // Token valid for 1 hour

            // Optionally, set the JWT token as a cookie
            Cookie authCookie = new Cookie(authenticationCookieName, jwtToken);
            authCookie.setHttpOnly(true); // Prevent JavaScript access for security
            authCookie.setPath("/"); // Set the path to make the cookie available across the app
            response.addCookie(authCookie);
            
            // Create user's cart, payment once account is created;
            CartAPIService cartAPIService = new CartAPIService();
            cartAPIService.createUserCart(userId, jwtToken);
            
            // Set session attributes for the logged-in user
            UserInfo user = new UserInfo(userId, firstname, lastname, email);
            request.getSession().setAttribute("userId", user.getUserId());
            request.getSession().setAttribute("userFirstname", user.getFirstname());
            request.getSession().setAttribute("userLastname", user.getLastname());

            // Forward to success page
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/loginsuccessful.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            // Handle any SQL exceptions such as email duplication or other database issues
            request.setAttribute("errorMessage", e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
            dispatcher.forward(request, response);
        } catch (Exception ex) {
            Logger.getLogger(SignupServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Capitalize the first letter of the first and last names
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public String getServletInfo() {
        return "SignupServlet for user registration and authentication.";
    }
}
