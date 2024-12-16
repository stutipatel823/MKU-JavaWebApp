<%@ page import="com.mku.helper.PaymentInfo" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String userFirstname = (String) session.getAttribute("userFirstname");
    PaymentInfo paymentInfo = (PaymentInfo) request.getAttribute("PaymentInfo");
    boolean isUpdate = paymentInfo != null;
%>

<html>

<head>
    <meta charset="UTF-8">
    <title>Payment Method</title>
    <link rel="stylesheet" type="text/css" href="/frontend/css/common.css"> 

    <script>
        function togglePaymentFields() {
            const paymentType = document.querySelector('input[name="paymentType"]:checked').value;
            const cardFields = document.getElementById("cardFields");
            const paypalFields = document.getElementById("paypalFields");
            
            const cardNumberField = document.getElementById("cardNumber");
            const expDateField = document.getElementById("expDate");
            const paypalEmailField = document.getElementById("paypalEmail");

            if (paymentType === "PayPal") {
                cardFields.style.display = "none";
                paypalFields.style.display = "block";
                cardNumberField.removeAttribute("required");
                expDateField.removeAttribute("required");
                paypalEmailField.setAttribute("required", "true");
            } else {
                cardFields.style.display = "block";
                paypalFields.style.display = "none";
                cardNumberField.setAttribute("required", "true");
                expDateField.setAttribute("required", "true");
                paypalEmailField.removeAttribute("required");
            }
        }

        window.onload = togglePaymentFields;  // Run togglePaymentFields on page load
    </script>

</head>
<body>
    <div class="container">
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/searchproducts" class="nav-link">Search Products</a>
            <a href="${pageContext.request.contextPath}/cart" class="nav-link">My Cart</a>
            <a href="${pageContext.request.contextPath}/orders" class="nav-link">My Orders</a>
            <a href="${pageContext.request.contextPath}/paymentmethod" class="nav-link" style="text-decoration: underline; color: darkturquoise;">My Payment</a>
            <a href="${pageContext.request.contextPath}/index.html" class="nav-link">Logout</a>
        </div>

        <div class="menu-container">
            <h2 class="title"><%= userFirstname%>'s Payment Method</h2>

            <% if (!isUpdate) { %>
                <p>You do not have a payment method linked to your account.</p>
            <% } else { %>
                <p>Payment Method: <%= paymentInfo.getPaymentMethod() %></p>
                <% if (paymentInfo.getPaymentMethod().equals("PayPal")) { %>
                    <p>PayPal Email: <%= paymentInfo.getPaypalEmail() %></p>
                <% } else { %>
                    <p>Card Number: <%= paymentInfo.getCardNumber() %></p>
                    <p>Expiration Date: <%= paymentInfo.getExpiryDate() %></p>
                <% } %>
            <% } %>

            <h3><%= isUpdate ? "Update" : "Add" %> Your Payment Method</h3>
            <form action="paymentmethod" method="post">

                <!-- Payment Type Radio Buttons -->
                <div class="payment-type">
                    <input type="radio" name="paymentType" value="Credit Card" onclick="togglePaymentFields()"
                           <% if (isUpdate && "Credit Card".equals(paymentInfo.getPaymentMethod())) { %>checked<% } else if (!isUpdate) { %>checked<% } %>> Credit Card

                    <input type="radio" name="paymentType" value="Debit Card" onclick="togglePaymentFields()"
                           <% if (isUpdate && "Debit Card".equals(paymentInfo.getPaymentMethod())) { %>checked<% } %>> Debit Card

                    <input type="radio" name="paymentType" value="PayPal" onclick="togglePaymentFields()"
                           <% if (isUpdate && paymentInfo.getPaymentMethod().equals("PayPal")) { %>checked<% } %>> PayPal
                </div>

                <!-- Card Payment Fields -->
                <div id="cardFields" style="display: <% if (isUpdate && !paymentInfo.getPaymentMethod().equals("PayPal")) { %>block<% } else { %>none<% } %>;">
                    <label for="cardNumber">Card Number</label>
                    <input type="text" placeholder="1234567890" name="cardNumber" class="form-input" id="cardNumber"
                           <% if (isUpdate && !paymentInfo.getPaymentMethod().equals("PayPal")) { %>value="<%= paymentInfo.getCardNumber() %>"<% } %> required maxlength="16" pattern="\d{16}" title="Card number must be 16 digits">
                    
                    <label for="expDate">Expiry Date</label>
                    <input type="month" name="expDate" class="form-input" id="expDate"
                           <% if (isUpdate && !paymentInfo.getPaymentMethod().equals("PayPal")) { %>value="<%= paymentInfo.getExpiryDate() %>"<% } %> required>
                </div>

                <!-- PayPal Payment Fields -->
                <div id="paypalFields" style="display: <% if (isUpdate && paymentInfo.getPaymentMethod().equals("PayPal")) { %>block<% } else { %>none<% } %>;">
                    <label for="paypalEmail">PayPal Email</label>
                    <input type="email" name="paypalEmail" class="form-input" id="paypalEmail" placeholder="your-email@example.com"
                           <% if (isUpdate && paymentInfo.getPaymentMethod().equals("PayPal")) { %>value="<%= paymentInfo.getPaypalEmail() %>"<% } %> required>
                </div>

                <!-- Submit Button -->
                <button type="submit" class="btn" style="background-color:pink; margin-top: 1rem;"><%= isUpdate ? "Update" : "Add" %> Payment Method</button>
            </form>
        </div>
    </div>
</body>
</html>
