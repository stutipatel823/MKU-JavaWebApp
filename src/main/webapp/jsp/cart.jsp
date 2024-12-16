<%@ page import="java.util.ArrayList" %>
<%@ page import="com.mku.helper.UserInfo" %>
<%@ page import="com.mku.helper.ProductInfo" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>My Cart</title>
    <link rel="stylesheet" type="text/css" href="/version3/css/common.css"> 
    <script>
        function placeOrder() {
            window.location.href = "<%= request.getContextPath() %>/confirmorder";
        }

        function deleteItem(productId) {
            let userId = <%= session.getAttribute("userId") %>;
            if (!userId || !productId) {
                alert("Invalid user or product data");
                return;
            }
            const url = `<%= request.getContextPath() %>/cart?userId=` + userId + "&productId=" + productId;
            fetch(url, { 
                method: "DELETE", 
                headers: { 
                    "Content-Type": "application/json" 
                }
            })
            .then(response => {
                if (response.ok) {
                    window.location.reload();  // Reload the page after successful deletion
                } else {
                    response.text().then(text => alert("Error: " + text));
                }
            })
            .catch(error => {
                console.error("Error deleting item:", error);
                alert("Error deleting item. Please try again.");
            });
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
                try {
                    String userFirstname = (String) session.getAttribute("userFirstname");
                    ArrayList<ProductInfo> products = (ArrayList<ProductInfo>) request.getAttribute("cartItems");
                    if (userFirstname != null) {
            %> 
                    <h2 class="title"> <%= userFirstname %>'s Cart</h2>
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
                                    <td><%= item.getPrice() * item.getQuantity() %></td>
                                    <td><%= item.getQuantity()%></td> 
                                    <td style="border:none">
                                        <button onclick="deleteItem('<%= item.getProductId() %>')">Delete</button>
                                    </td>
                                </tr>
                                 <% } %> 
                            </tbody>
                        </table>
                     <% } else { %>
                        <p>No items in cart.</p>  
                    <% } %>
                <% } else { %>
                    <p>Please log in to view your cart.</p>
                <% } %>
            <% } catch (Exception e) { %>
                <p>Error: <%= e.getMessage() %></p>
            <% } %>
            <button class="btn" style="background-color: pink; width:25%; margin-top: 1rem;" type="submit" onclick="placeOrder()">Order</button>
        </div>
    </div>
</body>
</html>
