package com.mku.business;

import com.mku.helper.PaymentInfo;
import com.mku.helper.PaymentsXML;
import com.mku.persistence.Payment_CRUD;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for Payment operations.
 */
public class PaymentService {

    public PaymentInfo getPaymentMethodByUserId(int userId) throws SQLException {
        PaymentInfo payment = Payment_CRUD.getPaymentMethodByUserId(userId);
        if (payment == null) {
            throw new IllegalArgumentException("No payment method found for user: " + userId);
        }
        return payment;
    }

    public int createPaymentMethodForUser(int userId, String paymentType, String cardNumber, String expDate, String paypalEmail) throws SQLException {
        PaymentInfo existingPayment = Payment_CRUD.getPaymentMethodByUserId(userId);
        if (existingPayment != null) {
            throw new IllegalArgumentException("User already has a payment method. Use updatePayment instead.");
        }

        return Payment_CRUD.createPaymentMethod(userId, paymentType, cardNumber, expDate, paypalEmail);
    }

    public void updatePaymentMethodForUser(int userId, String paymentType, String cardNumber, String expDate, String paypalEmail, boolean hasPaymentMethod) throws SQLException {
        boolean success = Payment_CRUD.updatePaymentMethod(userId, paymentType, cardNumber, expDate, paypalEmail, hasPaymentMethod);
        if (!success) {
            throw new SQLException("Failed to update payment method for user: " + userId);
        }
    }

    public void deletePaymentMethodForUser(int paymentId) throws SQLException {
        boolean success = Payment_CRUD.deletePaymentMethod(paymentId);
        if (!success) {
            throw new SQLException("Failed to delete payment method with ID: " + paymentId);
        }
    }


    public PaymentsXML getAllPaymentMethods() throws SQLException {
        List<PaymentInfo> paymentMethods = Payment_CRUD.getAllPaymentMethods();

        if (paymentMethods == null || paymentMethods.isEmpty()) {
            throw new SQLException("No payment methods found.");
        }

        // Wrap the list of payment methods in PaymentsXML
        PaymentsXML paymentsXML = new PaymentsXML(paymentMethods);
        return paymentsXML;
    }
}
