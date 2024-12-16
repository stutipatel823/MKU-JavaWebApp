<%@ page import="java.util.ArrayList" %>
<%@ page import="com.mku.version1.UserInfo" %>
<%@ page import="com.mku.version1.ProductInfo" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>My Cart</title>
    <link rel="stylesheet" type="text/css" href="/version1/css/common.css"> 
</head>

<body>
    <div class="container">
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/searchproducts" class="nav-link">Search Products</a>
            <a href="${pageContext.request.contextPath}/cart" class="nav-link" style="text-decoration: underline; color: darkturquoise;">My Cart</a>
            <a href="${pageContext.request.contextPath}/orders" class="nav-link">My Orders</a>
            <a href="${pageContext.request.contextPath}/index.html" class="nav-link">Logout</a>
        </div>
        
        <div class="menu-container">
             <% 
                UserInfo loggedInUser = (UserInfo) session.getAttribute("LoggedInUser");
                ArrayList<ProductInfo> products = (ArrayList<ProductInfo>) request.getAttribute("ProductsInCart");
            %> 
    
             <% if (loggedInUser != null) { %> 
                <h2 class="title"><%= loggedInUser.getFirstname() %>'s Cart</h2>
                 <% if (products != null && !products.isEmpty()) { %> 
                    <table>
                        <thead>
                            <tr>
                                <th>Product Name</th>
                                <th>Brand</th>
                                <th>Availability</th>
                                <th>Total Price</th>
                                <th>Quantity</th>
                                <th>Delete</th>
                            </tr>
                        </thead>
                        <tbody>
                             <% for (ProductInfo item : products) { %> 
                            <tr>
                                 <td><%= item.getName() %></td>
                                <td><%= item.getBrand() %></td>
                                <td><%= item.getAvailability() ? "Available" : "Out Of Stock" %></td>
                                <td><%= item.getPrice() %></td>
                                <td><%= item.getQuantity() %></td> 
                                <td style="border:none"><button>delete</button></td>
                            </tr>
                             <% } %> 
                        </tbody>
                    </table>
                 <% } else { %>
                    <p>Your cart is empty.</p>
                <% } %>
            <% } else { %>
                <p>Please log in to view your cart.</p>
            <% } %> 
        </div>
    </div>
</body>
</html>
