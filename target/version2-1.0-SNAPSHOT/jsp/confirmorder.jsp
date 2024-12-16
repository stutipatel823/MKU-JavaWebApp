<%@ page import="java.util.ArrayList" %>
<%@ page import="com.mku.version2.ProductInfo" %>
<%@ page import="com.mku.version2.PaymentInfo" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    ArrayList<ProductInfo> products = (ArrayList<ProductInfo>) request.getAttribute("products");
    PaymentInfo paymentInfo = (PaymentInfo) request.getAttribute("paymentInfo");
    boolean hasPaymentInfo = (paymentInfo != null);
    boolean hasProducts = (products != null && !products.isEmpty());
    double totalAmount = (double) request.getAttribute("totalAmount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirm Order</title>
    <link rel="stylesheet" href="/version2/css/common.css">
    <link rel="stylesheet" href="/version2/css/confirmorder.css">
    <script type="text/javascript">
        // JavaScript function to check products and payment info before confirming the order
        function checkProductsAndSubmit(hasProducts, hasPaymentInfo) {
            if (hasProducts && hasPaymentInfo) {
                // Confirm the order with the user before submitting the form
                var confirmation = confirm("Are you sure you want to place this order?");
                if (confirmation) {
                    // If confirmed, submit the form
                    document.getElementById("confirmOrderForm").submit();
                } else {
                    // If canceled, do nothing
                    return false;
                }
            } else if (!hasProducts) {
                alert("Your cart is empty.");
            } else if (!hasPaymentInfo) {
                alert("Payment method is missing.");
            }
        }
    </script>
</head>
<body>
    <div class="container">
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/searchproducts" class="nav-link">Search Products</a>
            <a href="${pageContext.request.contextPath}/cart" class="nav-link">My Cart</a>
            <a href="${pageContext.request.contextPath}/orders" class="nav-link">My Orders</a>
            <a href="${pageContext.request.contextPath}/index.html" class="nav-link">Logout</a>
        </div>

        <div class="menu-container">
            <h2 class="title">Confirm Your Order</h2>

            <h3>Products in Cart</h3>
            <table>
                <thead>
                    <tr>
                        <th>Product Name</th>
                        <th>Quantity</th>
                        <th>Price</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (hasProducts) {
                        for (ProductInfo product : products) { %>
                            <tr>
                                <td><%= product.getName() %></td>
                                <td><%= product.getQuantity() %></td>
                                <td>$<%= product.getPrice() %></td>
                            </tr>
                    <% } } else { %>
                            <tr><td colspan="3">No products in the cart.</td></tr>
                    <% } %>
                </tbody>
            </table>

            <p><strong>Total Amount:</strong> $<%= totalAmount %></p>

            <div class="payment-info">
                <h3>Payment Method</h3>
                <% if (paymentInfo != null) {
                        if (paymentInfo.getPaymentMethod().contains("Card")) { %>
                            <p><strong>Card Number:</strong> <%= paymentInfo.getCardNumber() %></p>
                            <p><strong>Expiration Date:</strong> <%= paymentInfo.getExpiryDate() %></p>
                    <% } else if ("paypal".equalsIgnoreCase(paymentInfo.getPaymentMethod())) { %>
                            <p><strong>PayPal Email:</strong> <%= paymentInfo.getPaypalEmail() %></p>
                    <% } %>
                    <a href="<%= request.getContextPath() %>/paymentMethod">Change Payment Method</a>
                <% } else { %>
                    <p>No payment method found. <a href="<%= request.getContextPath() %>/paymentMethod" style="color:orangered;">Add Payment Method</a></p>
                <% } %>
            </div>

            <!-- Form with a button that calls the JavaScript function on click -->
            <form id="confirmOrderForm" action="<%= request.getContextPath() %>/placeorder" method="post">
                <button type="button" class="btn" style="background-color:pink;"
                        onclick="checkProductsAndSubmit(<%= hasProducts %>, <%= hasPaymentInfo %>)">
                    Confirm Order
                </button>
            </form>
        </div>
    </div>
</body>
</html>
