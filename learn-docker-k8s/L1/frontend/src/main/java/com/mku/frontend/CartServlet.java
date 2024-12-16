package com.mku.frontend;

import com.mku.business.CartAPIService;
import com.mku.helper.ProductInfo;
import com.mku.helper.ProductsXML;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CartServlet.class.getName());
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
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

        try (CartAPIService cartAPIService = new CartAPIService()) {
            ProductsXML productsXML= cartAPIService.fetchCartItems(userId, token);
            ArrayList<ProductInfo> cartItems = productsXML.getProducts();
            
            // Check if cartItems is null and replace it with an empty list
            if(cartItems == null){
                cartItems = new ArrayList<>();
            }
            request.setAttribute("cartItems", cartItems);
            RequestDispatcher dispatcher = request.getRequestDispatcher("jsp/cart.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing cart request for userId: " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = getUserToken(request);
        if (token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in.");
            return;
        }

        String productId = request.getParameter("productId");
        if (productId == null || productId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is required.");
            return;
        }

        try (CartAPIService cartService = new CartAPIService()) {
            cartService.deleteCartItem(userId, productId, token);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting cart item for userId: " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
