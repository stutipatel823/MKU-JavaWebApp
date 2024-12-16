<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mku.version2.UserInfo" %>
<%@page import="com.mku.version2.OrderInfo"%>
<%@page import="com.mku.version2.ProductInfo"%>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>

<%
    UserInfo user = (UserInfo) session.getAttribute("LoggedInUser");
    ArrayList<OrderInfo> orders = (ArrayList<OrderInfo>) request.getAttribute("orders");

    // Create the SimpleDateFormat object to parse and format the date
    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>
<!DOCTYPE html>
<html>

<head>
    <title>My Orders</title>
    <link rel="stylesheet" type="text/css" href="/version2/css/common.css"> 
    <link rel="stylesheet" type="text/css" href="/version2/css/orders.css"> 
</head>


<body>
    <div class="container">
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/searchproducts" class="nav-link">Search Products</a>
            <a href="${pageContext.request.contextPath}/cart" class="nav-link">My Cart</a>
            <a href="${pageContext.request.contextPath}/orders" class="nav-link" style="text-decoration: underline; color: darkturquoise;"> My Orders</a>
            <a href="${pageContext.request.contextPath}/index.html" class="nav-link">Logout</a>
        </div>

        <h1 class="title">
            <%= user.getFirstname() %>'s Orders
        </h1>
        <div class="menu-container">
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
                                    for (ProductInfo product : order.getProducts()) {
                                %>
                                    <li style=" list-style-type: none;">
                                        - <%= product.getName() %> x<%= product.getQuantity() %> 
                                        ($<%= product.getPrice()* product.getQuantity()%>)
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
        </div>
    </div>
</body>

</html>
