package com.mku.frontend;

import com.mku.business.OrderService;
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
import javax.servlet.RequestDispatcher;

@WebServlet(name = "ConfirmOrderServlet", urlPatterns = {"/confirmorder"})
public class ConfirmOrderServlet extends HttpServlet {

    private OrderService orderService = new OrderService();  // Business logic layer

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        if (userId == null || userId <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valid User ID is required.");
            return;
        }

        try {
            // Fetch Cart Data
            ProductsXML cartData = orderService.getCartDataByApiUrl(userId);
            ArrayList<ProductInfo> cartItems = cartData.getProducts();

            // Calculate total price
            double totalPrice = 0.0;
            for (ProductInfo product : cartItems) {
                totalPrice += product.getPrice() * product.getQuantity();
            }

            // Fetch Payment Data
            PaymentInfo paymentInfo = orderService.getPaymentDataByApiUrl(userId);

            // Set attributes for the JSP
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("totalPrice", totalPrice);
            request.setAttribute("paymentInfo", paymentInfo);

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/confirmorder.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing the order.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null || userId <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valid User ID is required.");
            return;
        }

        try {
            // Fetch Cart Data
            ProductsXML cartItems = orderService.getCartDataByApiUrl(userId);

            // Ensure cart data is valid and has products
            if (cartItems == null || cartItems.getProducts().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cart is empty.");
                return;
            }

            // Fetch Payment Data
            PaymentInfo paymentInfo = orderService.getPaymentDataByApiUrl(userId);

            // Ensure payment data is valid
            if (paymentInfo == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Payment method is missing.");
                return;
            }

            // The cartId and paymentId are assumed to be the same as userId as per your explanation
            int cartId = userId;  // Assuming cartId is the same as userId
            int paymentId = userId;  // Assuming paymentId is the same as userId

            // Create the order by passing the required parameters
            int orderId = orderService.createOrder(userId, cartId, paymentId, cartItems);
            System.out.println("Order Placed with OrderId:" + orderId);
            // After order creation, you can redirect to a confirmation page or display the order details
            response.sendRedirect(request.getContextPath() + "/orders");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error placing the order.");
        }
    }

}