package com.mku.frontend;

import com.mku.business.PaymentAPIService;
import com.mku.helper.PaymentInfo;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;

@WebServlet(name = "PaymentMethodServlet", urlPatterns = {"/paymentmethod"})
public class PaymentMethodServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(PaymentMethodServlet.class.getName());
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

        try (PaymentAPIService paymentAPIService = new PaymentAPIService()) {
            // Fetch Payment Data
            PaymentInfo paymentInfo = paymentAPIService.fetchPaymentMethod(userId, token);
            
            // Set attributes for the JSP
            request.setAttribute("PaymentInfo", paymentInfo);

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/paymentmethod.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing the payment method page.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing the payment.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        String paymentType = request.getParameter("paymentType");
        String cardNumber = request.getParameter("cardNumber");
        String expDate = request.getParameter("expDate");
        String paypalEmail = request.getParameter("paypalEmail");

        String token = getUserToken(request);
        if (token == null || token.isEmpty()) {
            LOGGER.warning("Unauthorized access attempt. No token found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        try (PaymentAPIService paymentAPIService = new PaymentAPIService()) {
            // Check if the user has a payment method
            PaymentInfo existingPaymentInfo = paymentAPIService.fetchPaymentMethod(userId, token);

            if (existingPaymentInfo == null) {
                // No existing payment method; call "add" endpoint
                paymentAPIService.addPaymentMethod(userId, token, paymentType, cardNumber, expDate, paypalEmail);
                LOGGER.info("Successfully added payment method for userId: " + userId);
            } else {
                // Existing payment method; call "update" endpoint
                paymentAPIService.updatePaymentMethod(userId, token, paymentType, cardNumber, expDate, paypalEmail);
                LOGGER.info("Successfully updated payment method for userId: " + userId);
            }

            // Redirect back to the payment method page after submission
            response.sendRedirect(request.getContextPath() + "/paymentmethod");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing payment method for userId: " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing payment method.");
        }
    }

}
