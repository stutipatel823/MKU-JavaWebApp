package com.mku.frontend;

import com.mku.business.UserService;
import java.io.IOException;
import com.mku.helper.UserInfo;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

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
//        email = "alice@example.com";
        password = "password123";

        try {
            UserInfo user = userService.authenticateUser(email, password);
            
            if(user != null){
                System.out.println("User authenticated: " + user.getFirstname() + " " + user.getLastname());
                
                request.getSession().setAttribute("userId", user.getUserId());
                request.getSession().setAttribute("userFirstname", user.getFirstname());
                request.getSession().setAttribute("userLastname", user.getLastname());
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/loginsuccessful.jsp");
                dispatcher.forward(request, response);

            }
            else {
                // User authentication failed
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
