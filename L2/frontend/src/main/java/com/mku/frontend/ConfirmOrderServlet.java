package com.mku.fromtend;

import com.mku.business.CartAPIService;
import com.mku.business.OrderAPIService;
import com.mku.business.PaymentAPIService;
import com.mku.helper.PaymentInfo;
import com.mku.helper.ProductsXML;
import com.mku.helper.ProductInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;

@WebServlet(name = "ConfirmOrderServlet", urlPatterns = {"/confirmorder"})
public class ConfirmOrderServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ConfirmOrderServlet.class.getName());
    private final String authenticationCookieName = "login_token";

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
    
    private ProductsXML getCartItems(int userId, String token) throws IOException {
        try (CartAPIService cartAPIService = new CartAPIService()) {
            // Fetch the cart items via the CartAPIService and return ProductsXML object
            return cartAPIService.fetchCartItems(userId, token);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing cart request for userId: " + userId, e);
            throw new IOException("Error fetching cart items", e);
        }
    }
    
    private double getTotalPrice(ArrayList<ProductInfo> cartItems){
        double totalPrice = 0.0;
        for (ProductInfo product : cartItems) {
            totalPrice += product.getPrice() * product.getQuantity();
        }
        return totalPrice;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        try {
            // Fetch Cart Data
            ProductsXML productsXML = getCartItems(userId, token);
            ArrayList<ProductInfo> cartItems = productsXML.getProducts();

            // Calculate total price
            double totalPrice = getTotalPrice(cartItems);

            // Fetch Payment Data
            PaymentAPIService paymentAPIService = new PaymentAPIService();
            PaymentInfo paymentInfo = paymentAPIService.fetchPaymentMethod(userId, token);

            // Set attributes for the JSP
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("totalPrice", totalPrice);
            request.setAttribute("paymentInfo", paymentInfo);

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/confirmorder.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing the order confirmation page.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing the order.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        try {
            // Fetch cart items and calculate total
            ProductsXML cartDataXML = getCartItems(userId, token);
            int paymentId = userId; // Assuming paymentId equals userId

            // Place the order using the OrderAPIService
            try (OrderAPIService orderAPIService = new OrderAPIService()) {
                orderAPIService.placeOrder(userId, userId, paymentId, cartDataXML, token);
                LOGGER.info("Order placed successfully for userId: " + userId);
            }

//            // Empty the user's cart after successful order placement
//            try (CartAPIService cartAPIService = new CartAPIService()) {
//                cartAPIService.emptyUserCart(userId, null, token);
//                LOGGER.info("Cart emptied successfully for userId: " + userId);
//            } catch (Exception e) {
//                LOGGER.log(Level.WARNING, "Failed to empty cart for userId: " + userId, e);
//            }

            // Redirect to the orders page after successful order
            response.sendRedirect(request.getContextPath() + "/orders");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing the order for userId: " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error placing the order.");
        }
    }

}
