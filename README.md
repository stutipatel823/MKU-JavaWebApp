# Version 2: Enhancements and Cart Management

## Overview

This updated version of the project introduces advanced cart management functionalities, including the ability to **add**, **remove**, and **place orders** directly from the cart. These features were implemented using a combination of **JavaScript** for frontend interactions and **Java Servlets** to handle the logic and database communication. The project integrates with a MySQL database to persist the cart items, process orders, and maintain an updated view of the user's cart.

### Key Features in Version 2:
- Add items to the cart dynamically via JavaScript and Servlet communication.
- Remove items from the cart with confirmation.
- Place an order after confirming the cart and payment details.

## Add to Cart (searchproducts.jsp)

The **"Add to Cart"** functionality in this version is handled by a **JavaScript function** that sends a request to the server using the **Fetch API**. This JavaScript function triggers the **SearchProductsServlet** to process the cart addition in the database.

**JavaScript:**
```javascript
function addItem(productId) {
    fetch("<%= request.getContextPath() %>/searchproducts", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "action=add&productId=" + productId
    })
    .then(response => {
        if (response.ok) {
            alert("Product added to cart.");
            window.location.reload();
        } else {
            alert("Failed to add product to cart. Please try again.");
        }
    })
    .catch(error => console.error('Error:', error));
}
```

The **SearchProductsServlet** handles the logic for adding a product to the user's cart. If the user is logged in, the servlet inserts the product into the **Cart_Product** table in the database.

```java
private void addProduct(int userId, int productId, HttpServletResponse response) throws IOException {
    String insertQuery = "INSERT INTO Cart_Product (cart_id, product_id, quantity) VALUES (?, ?, 1) " +
                         "ON DUPLICATE KEY UPDATE quantity = quantity + 1";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
         PreparedStatement pst = conn.prepareStatement(insertQuery)) {

        pst.setInt(1, userId); 
        pst.setInt(2, productId);

        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add product to cart.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add product.");
    }
}
```

## Remove Item from Cart (cart.jsp)

Similarly, removing items from the cart is handled by a **JavaScript function** that sends a **POST request** to the server when the user confirms the removal.

**JavaScript:**
```javascript
function deleteItem(productId) {
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
```

This function sends the product ID to the **CartServlet**, which processes the removal by deleting the corresponding entry from the **Cart_Product** table.

**Servlet Code (CartServlet):**
```java
private void deleteProduct(int userId, int productId, HttpServletResponse response) throws IOException {
    String deleteQuery = "DELETE FROM Cart_Product WHERE cart_id = ? AND product_id = ?";
    
    try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
         PreparedStatement pst = conn.prepareStatement(deleteQuery)) {
        pst.setInt(1, userId); 
        pst.setInt(2, productId);

        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found in cart.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete product.");
    }
}
```

## Place an Order (confirmorder.jsp)

When the user is ready to place an order, the **"Place Order"** button is used. This action first checks if the cart has products and if the user has provided payment information.

**JavaScript:**
```javascript
function checkProductsAndSubmit(hasProducts, hasPaymentInfo) {
    if (hasProducts && hasPaymentInfo) {
        var confirmation = confirm("Are you sure you want to place this order?");
        if (confirmation) {
            document.getElementById("confirmOrderForm").submit();
        } else {
            return false;
        }
    } else if (!hasProducts) {
        alert("Your cart is empty.");
    } else if (!hasPaymentInfo) {
        alert("Payment method is missing.");
    }
}
```

Once confirmed, the form is submitted to the server, and the **PlaceOrderServlet** is invoked to process the order by inserting the order details and associated products into the **Order** and **Order_Product** tables.

**Servlet Code (PlaceOrderServlet):**
```java
protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    ArrayList<ProductInfo> products = (ArrayList<ProductInfo>) request.getSession().getAttribute("ProductsInCart");
    UserInfo user = (UserInfo) request.getSession().getAttribute("LoggedInUser");
    int userId = user.getUserId();

    // Ensure the cart is not empty
    if (products == null || products.isEmpty()) {
        out.println("No products found in the cart.");
        return;
    }

    // Calculate the total amount of the order
    double totalAmount = 0.0;
    for (ProductInfo product : products) {
        totalAmount += product.getPrice() * product.getQuantity();
    }

    String status = "Pending";

    // SQL queries
    String insertOrderQuery = "INSERT INTO `Order` (order_date, amount, transaction_date, status, user_id, cart_id, payment_id) VALUES (NOW(), ?, NOW(), ?, ?, ?, ?)";
    String insertOrderProductQuery = "INSERT INTO Order_Product (order_id, product_id, quantity) VALUES (?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword)) {
        // Insert order into Order table
        PreparedStatement pst = conn.prepareStatement(insertOrderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setDouble(1, totalAmount);
        pst.setString(2, status);
        pst.setInt(3, userId);
        pst.setInt(4, userId);  // Assuming cartId is 0 or can be retrieved
        pst.setInt(5, paymentId); // Fetch paymentId from database
        pst.executeUpdate();

        // Retrieve the generated order ID
        int orderId = pst.getGeneratedKeys().getInt(1);

        // Insert each product into the Order_Product table
        for (ProductInfo product : products) {
            PreparedStatement pst2 = conn.prepareStatement(insertOrderProductQuery);
            pst2.setInt(1, orderId);
            pst2.setInt(2, product.getProductId());
            pst2.setInt(3, product.getQuantity());
            pst2.executeUpdate();
        }

        // Redirect to the order confirmation page
        response.sendRedirect(request.getContextPath() + "/orders");

    } catch (SQLException e) {
        e.printStackTrace();
        response.getWriter().println("An error occurred while placing the order.");
    }
}
```

## Conclusion

In **Version 2**, the cart functionality is enhanced with the ability to **add items**, **remove items**, and **place orders** directly from the shopping cart. The implementation uses **JavaScript** to trigger **POST requests** to Java **Servlets**, which interact with the **MySQL database** to update and manage the cart and orders.

This version brings a more interactive and seamless shopping experience, enabling users to manage their cart more effectively and place orders with ease.

