package com.mku.frontend;

import com.mku.business.Authenticate;
import com.mku.business.UserService;
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

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final Authenticate autho = new Authenticate();
    private final String authenticationCookieName = "login_token";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("/jsp/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email").trim();
        String password = request.getParameter("password").trim();
        password = "password123";
        
        try {
            UserInfo user = userService.authenticateUser(email, password);
            System.out.println("<UserService> userId: "+user.getUserId());
            if (user != null) {
                
                // User authenticated successfully
                System.out.println("User authenticated: " + user.getFirstname() + " " + user.getLastname());

                // Generate a JWT for the user
                String token = autho.createJWT("login-token", user.getEmail(), 3600000); // 1-hour token

                // Add the token to the response as a cookie
                Cookie authCookie = new Cookie(authenticationCookieName, token);
                authCookie.setHttpOnly(true); // Prevent JavaScript access for security
                authCookie.setPath("/"); // Set the path to make the cookie available across the app
                response.addCookie(authCookie);

                // Set session attributes
                request.getSession().setAttribute("userId", user.getUserId());
                request.getSession().setAttribute("userFirstname", user.getFirstname());
                request.getSession().setAttribute("userLastname", user.getLastname());

                // Forward to success page
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/loginsuccessful.jsp");
                dispatcher.forward(request, response);
            } else {
                // Authentication failed
                System.out.println("Invalid email or password.");
                request.getRequestDispatcher("/jsp/loginfailed.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during login.");
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user login and forwards to appropriate views based on authentication result.";
    }
}
