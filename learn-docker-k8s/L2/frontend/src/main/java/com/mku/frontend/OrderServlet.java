package com.mku.frontend;

import com.mku.business.OrderAPIService;
import com.mku.helper.OrdersXML;
import com.mku.helper.OrderInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "OrderServlet", urlPatterns = {"/orders"})
public class OrderServlet extends HttpServlet {
    
    // Fixed: Logger initialization for the correct class
    private static final Logger LOGGER = Logger.getLogger(OrderServlet.class.getName());
    private final String authenticationCookieName = "login_token";

    // Helper method to get user token from cookies
    private String getUserToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(authenticationCookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = getUserToken(request);
        if (token == null || token.isEmpty()) {
            LOGGER.warning("Unauthorized access attempt. No token found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            LOGGER.warning("Unauthorized access attempt. No userId found in session.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in.");
            return;
        }

        // Create the orderAPIService object without try-with-resources
        OrderAPIService orderAPIService = new OrderAPIService();

        try {
            OrdersXML ordersXML = orderAPIService.fetchOrders(userId, token);
            ArrayList<OrderInfo> orders = ordersXML.getOrders();

            request.setAttribute("orders", orders);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/orders.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing order request for userId: " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching orders.");
        } 
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    public String getServletInfo() {
        return "OrderServlet to manage user's orders.";
    }
}
