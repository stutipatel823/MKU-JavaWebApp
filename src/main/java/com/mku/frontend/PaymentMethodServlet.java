package com.mku.frontend;

import com.mku.business.PaymentService;
import com.mku.helper.PaymentInfo;
import java.io.IOException;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "PaymentMethodServlet", urlPatterns = {"/paymentmethod"})
public class PaymentMethodServlet extends HttpServlet {
    private PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        
        try {
            PaymentInfo paymentInfo = paymentService.getPaymentMethodByUserId(userId);
            request.setAttribute("userPaymentInfo", paymentInfo);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/paymentmethod.jsp");
            dispatcher.forward(request, response);
            
        } catch (SQLException ex) {
            Logger.getLogger(PaymentMethodServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        
        System.out.println(expDate);
        try {
            paymentService.updatePaymentMethodForUser(userId, paymentType, cardNumber, expDate, paypalEmail, true);
        } catch (SQLException ex) {
            Logger.getLogger(PaymentMethodServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Redirect back to the payment method page after submission
        response.sendRedirect(request.getContextPath() + "/paymentmethod");
        
     
    }
    @Override
    public String getServletInfo() {
        return "Servlet to handle payment method submission.";
    }
}
