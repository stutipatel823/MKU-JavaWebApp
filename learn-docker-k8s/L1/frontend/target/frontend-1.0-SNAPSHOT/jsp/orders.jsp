<%@page import="com.mku.helper.ProductsXML"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mku.helper.UserInfo" %>
<%@page import="com.mku.helper.OrderInfo"%>
<%@page import="com.mku.helper.ProductInfo"%>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>

<%
    String userFirstname = (String) session.getAttribute("userFirstname");
    ArrayList<OrderInfo> orders = (ArrayList<OrderInfo>) request.getAttribute("orders");

    // Create the SimpleDateFormat object to parse and format the date
    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>
<!DOCTYPE html>
<html>

<head>
    <title>My Orders</title>
    <link rel="stylesheet" type="text/css" href="/version3/css/common.css"> 
    <link rel="stylesheet" type="text/css" href="/version3/css/orders.css"> 
</head>

<body>
    <div class="container">
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/searchproducts" class="nav-link">Search Products</a>
            <a href="${pageContext.request.contextPath}/cart" class="nav-link">My Cart</a>
            <a href="${pageContext.request.contextPath}/orders" class="nav-link" style="text-decoration: underline; color: darkturquoise;"> My Orders</a>
            <a href="${pageContext.request.contextPath}/paymentmethod" class="nav-link">My Payment</a>
            <a href="${pageContext.request.contextPath}/index.html" class="nav-link">Logout</a>
        </div>

        <h1 class="title">
            <%= userFirstname %>'s Orders
        </h1>

        <div class="menu-container">
            <%
                if (orders == null || orders.isEmpty()) {
            %>
                <p>No orders placed yet.</p>
            <%
                } else {
            %>
                <table>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Items</th>
                            <th>Total Price</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (OrderInfo order : orders) {
                                // Parse and format the order date
                                String orderDateStr = order.getOrderDate(); // "2024-10-01 12:00:00"
                                String formattedDate = "";
                                try {
                                    Date parsedDate = inputDateFormat.parse(orderDateStr);
                                    formattedDate = outputDateFormat.format(parsedDate);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        %>
                            <tr>
                                <td><%= formattedDate %></td>
                                <td>
                                    <% 
                                        // Display products in the order and calculate total price
                                        ProductsXML productsXML = order.getProductsOrdered();
                                        ArrayList<ProductInfo> products = productsXML.getProducts();
                                        for (ProductInfo product : products) {
                                    %>
                                        <li style=" list-style-type: none;">
                                            - <%= product.getName() %> x<%= product.getQuantity() %> 
                                            ($<%= product.getPrice() * product.getQuantity() %>)
                                        </li>
                                    <% } %>
                                </td>
                                <td>$<%= order.getAmount() %></td>
                                <td><%= order.getStatus() %></td>  <!-- Display the order status -->
                            </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            <%
                }
            %>
        </div>
    </div>
</body>

</html>
