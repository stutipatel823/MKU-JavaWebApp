<%@ page import="java.util.ArrayList" %>
<%@ page import="com.mku.version2.UserInfo" %>
<%@ page import="com.mku.version2.ProductInfo" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>My Cart</title>
    <link rel="stylesheet" type="text/css" href="/version2/css/common.css"> 
    <script>
        function placeOrder() {
            window.location.href = "<%= request.getContextPath() %>/confirmorder";  // Modify to your actual order page URL
        }
        
        function deleteItem(productId){
            if (confirm("Are you sure you want to delete this item from your cart?")) {
                fetch("<%= request.getContextPath() %>/cart", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "action=delete&productId=" + productId
                })
                .then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert("Failed to delete item. Please try again.");
                    }
                });
            }
        }

    </script>
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
                ArrayList<ProductInfo> products = (ArrayList<ProductInfo>) session.getAttribute("ProductsInCart");
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
                                <td style="border:none"><button onclick="deleteItem(<%= item.getProductId()%>)">delete</button></td>
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
            <button class="btn" style="background-color: pink; width:25%; margin-top: 1rem;" type="submit" onclick="placeOrder()">Order</button>
        </div>
        
    </div>
</body>
</html>
