<%--version3--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.mku.helper.ProductInfo" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Product Search</title>
    <link rel="stylesheet" type="text/css" href="/version3/css/searchproducts.css">
    <link rel="stylesheet" type="text/css" href="/version3/css/common.css"> 
    <script>
        function submitForm() {
            document.getElementById("searchForm").submit();

        }
        
        function addItemToCart(productId) {
            let userId = <%= session.getAttribute("userId") %>;  // Assuming userId is stored in the session

            // Ensure userId and productId are valid
            if (!userId || !productId) {
                alert("Invalid user or product data");
                return;
            }

            // Construct the URL to call the servlet (not the Cart microservice directly)
            const url = `<%= request.getContextPath() %>/searchproducts`;  // Servlet endpoint
            const params = new URLSearchParams();
            params.append('action', 'addToCart');  // Action to add to cart
            params.append('userId', userId);
            params.append('productId', productId);

            // Send the POST request to the servlet
            fetch(url, {
                method: 'POST',
                body: params,  // URL-encoded form data
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',  // URL-encoded format
                },
            })
            .then(response => response.text())  // Expecting plain text response
            .then(data => {
                if (data === "success") {
                    alert("Item added to cart!");
                } else {
                    alert("Failed to add item to cart.");
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("Error occurred while adding item to cart. Please try again.");
            });
        }
    </script>
</head>

<%

    ArrayList<ProductInfo> products = (ArrayList<ProductInfo>) request.getAttribute("AllProducts");

    String categoryValue = (String) request.getAttribute("categoryValue");

    String brandValue = (String) request.getAttribute("brandValue");

%>
<body>
    <div class="container">
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/searchproducts" class="nav-link" style="text-decoration: underline; color: darkturquoise;">Search Products</a>
            <a href="${pageContext.request.contextPath}/cart" class="nav-link">My Cart</a>
            <a href="${pageContext.request.contextPath}/orders" class="nav-link">My Orders</a>
            <a href="${pageContext.request.contextPath}/index.html" class="nav-link">Logout</a>
        </div>

        <div class="product-search-container">
            <form id="searchForm" method="post" action="searchproducts" class="search-form" style="display: flex; width: 100%;">
                <div class="search-input-container">
                    <label for="searchInput">Search:</label>
                    <input type="text" id="searchInput" name="searchInput" placeholder="Enter product name">
                    <button type="submit">Search</button>
                </div>
        
                <div class="filter-input-container">
                    <label for="brandSelect">Brand:</label>
                    <select id="brandSelect" name="brandSelect" onChange="submitForm()">
                        <option value="">All</option>
                        <% ArrayList<String> brands = (ArrayList<String>) request.getAttribute("brands");
                            for (String brand : brands) { 
                        %>
                            <option value="<%= brand %>" <%= brand.equals(brandValue) ? "selected" : "" %>> <%= brand %> </option>
                        <% 
                            }
                        %>
                    </select>
                </div>
        
                <div class="filter-input-container">
                    <label for="categorySelect">Category:</label>
                    <select id="categorySelect" name="categorySelect" onChange="submitForm()">
                        <option value="">All</option>
                        <% ArrayList<String> categories = (ArrayList<String>) request.getAttribute("categories");
                            for (String category : categories) { 
                        %>
                            <option value="<%= category %>" <%= category.equals(categoryValue) ? "selected" : "" %>> <%= category %> </option>
                        <% 
                            }
                        %>
                    </select>
                </div>
            </form>
        </div>
        

        <div class="menu-container">
            <h2 class="title">Product Search Results</h2>
            <table>
                <thead>
                    <tr>
                        <th>Product ID</th>
                        <th>Product Name</th>
                        <th>Price</th>
                        <th>Brand</th>
                        <th>Category</th>
                        <th>Stock</th>
                        <th>Availability</th>
                        <!-- <th>Actions</th> -->
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (ProductInfo product : products) {
                    %>
                    <tr>
                        <td><%= product.getProductId() %></td>
                        <td><%= product.getName() %></td>
                        <td><%= product.getPrice() %></td>
                        <td><%= product.getBrand() %></td>
                        <td><%= product.getCategory() %></td>
                        <td><%= product.getStock() %></td>
                        <td><%= product.getAvailability() ? "Available" : "Out Of Stock" %></td>
                        <td style="border:none">
                            <button onclick="addItemToCart(<%= product.getProductId() %>)">Add to Cart</button>
                        </td>
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
