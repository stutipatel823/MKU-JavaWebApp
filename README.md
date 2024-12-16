# Version 3: N-Layer Architecture with Clear Layer Responsibilities

## Overview

In **Version 3**, the project follows a **N-layer architecture** that separates the responsibilities of various layers within the application. This architecture provides greater flexibility, modularity, and maintainability by organizing the system into distinct layers: **GUI Layer**, **Resources Layer**, **Business Layer**, **Persistence Layer**, and **Helper Layer**.

### Key Features in Version 3:
- **Clear separation of concerns** between presentation, business logic, and data access.
- **Business Logic** in the **Business Layer** handles the core functionality.
- **Persistence Layer** interacts directly with the database, performing CRUD operations.
- **Helper Layer** provides POJOs and utility classes for data transfer and serialization.
- **Resources Layer** exposes RESTful APIs to external consumers.
- **GUI Layer** interacts with the **Business Layer** to perform actions like managing payment methods.

## Layer Breakdown and Interactions

Each layer in the **N-layer architecture** plays a distinct role, and their interactions follow a clear pattern:

### 1. **GUI Layer**: User Interface
- The **GUI Layer** consists of **Java Servlets** that handle incoming HTTP requests, process user input, and forward responses.
- Servlets in this layer make calls to the **Business Layer** to perform operations like retrieving and updating data.
- The **GUI Layer** does not contain any business logic but acts as an intermediary between the user and the business logic.

**Example: PaymentMethodServlet**

```java
private PaymentService paymentService = new PaymentService();

@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    Integer userId = (Integer) request.getSession().getAttribute("userId");
    
    try {
        // Calls the Business Layer to retrieve payment method data
        PaymentInfo paymentInfo = paymentService.getPaymentMethodByUserId(userId);
        request.setAttribute("userPaymentInfo", paymentInfo);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/paymentmethod.jsp");
        dispatcher.forward(request, response);
    } catch (SQLException ex) {
        Logger.getLogger(PaymentMethodServlet.class.getName()).log(Level.SEVERE, null, ex);
    }
}
```

- The **PaymentMethodServlet** calls the **`PaymentService`** (Business Layer) to retrieve the payment method associated with a user by calling `paymentService.getPaymentMethodByUserId(userId)`.
- The servlet then forwards the **PaymentInfo** object to the **JSP** page for rendering.

### 2. **Resources Layer**: Exposes RESTful APIs
- The **Resources Layer** provides **RESTful web services** to interact with the application programmatically.
- It receives HTTP requests, processes them, and interacts with the **Business Layer** for performing operations.
- The **Resources Layer** is typically used for external systems to consume data or interact with the business logic.

**Example: PaymentResource API**

```java
@Path("/payments")
public class PaymentResource {

    private PaymentService paymentService = new PaymentService();

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllPaymentMethods() {
        try {
            // Calls the Business Layer to retrieve all payment methods
            List<PaymentInfo> paymentMethods = paymentService.getAllPaymentMethods();
            PaymentsXML paymentsXML = new PaymentsXML(paymentMethods);
            return Response.ok(paymentsXML).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error retrieving payment methods").build();
        }
    }
}
```

- The **`PaymentResource`** class in the **Resources Layer** interacts with the **Business Layer** by calling `paymentService.getAllPaymentMethods()` to fetch a list of payment methods.
- The retrieved data is then converted into XML format (using **JAXB**) and returned as a **response**.

### 3. **Business Layer**: Core Logic and Service Layer
- The **Business Layer** contains the core business logic of the application. It processes data, applies business rules, and interacts with the **Persistence Layer** to store or retrieve data from the database.
- This layer ensures the system behaves according to the business requirements.
- It acts as a mediator between the **GUI Layer**, **Resources Layer**, and **Persistence Layer**.

**Example: PaymentService (Business Layer)**

```java
public class PaymentService {

    private Payment_CRUD paymentCRUD = new Payment_CRUD();

    public PaymentInfo getPaymentMethodByUserId(int userId) throws SQLException {
        return paymentCRUD.getPaymentMethodByUserId(userId);
    }

    public void updatePaymentMethodForUser(int userId, String paymentType, String cardNumber, String expDate, String paypalEmail) throws SQLException {
        paymentCRUD.updatePaymentMethodForUser(userId, paymentType, cardNumber, expDate, paypalEmail);
    }
}
```

- The **`PaymentService`** class is responsible for invoking the **Persistence Layer** via the **`Payment_CRUD`** class to interact with the database.
- The **Business Layer** can contain other logic like validation, calculating totals, and applying business rules, which it applies before sending data to the **Persistence Layer**.

### 4. **Persistence Layer**: Database Access
- The **Persistence Layer** interacts directly with the **MySQL database** using **JDBC** to perform CRUD operations.
- It receives requests from the **Business Layer** to retrieve, insert, update, or delete data.
- It is the lowest layer in the stack that handles all database interactions.

**Example: Payment_CRUD (Persistence Layer)**

```java
public class Payment_CRUD {

    public PaymentInfo getPaymentMethodByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM Payment WHERE user_id = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PaymentInfo(rs.getInt("payment_id"), 
                                           rs.getString("payment_method"), 
                                           rs.getString("card_number"), 
                                           rs.getString("card_expiration_date"), 
                                           rs.getString("paypal_email"));
                }
            }
        }
        return null;
    }

    public void updatePaymentMethodForUser(int userId, String paymentType, String cardNumber, String expDate, String paypalEmail) throws SQLException {
        String query = "UPDATE Payment SET payment_method = ?, card_number = ?, card_expiration_date = ?, paypal_email = ? WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, paymentType);
            stmt.setString(2, cardNumber);
            stmt.setString(3, expDate);
            stmt.setString(4, paypalEmail);
            stmt.setInt(5, userId);
            stmt.executeUpdate();
        }
    }
}
```

- The **`Payment_CRUD`** class is responsible for directly interacting with the database, using **JDBC** to retrieve payment data (`getPaymentMethodByUserId`) or update it (`updatePaymentMethodForUser`).
- It handles all database-related tasks and ensures the **Business Layer** receives the necessary data in the required format.

### 5. **Helper Layer**: Data Transfer Objects (DTOs)
- The **Helper Layer** provides **POJOs (Plain Old Java Objects)** that are used to transfer data between the layers.
- These objects represent the data in a structured format and can also be used for **XML serialization**.

**Example: PaymentInfo and PaymentsXML (Helper Layer)**

```java
public class PaymentInfo {
    private int paymentId;
    private String paymentMethod;
    private String cardNumber;
    private String expDate;
    private String paypalEmail;
    private int userId;

    // Getters and Setters
}

@XmlRootElement(name = "Payments")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentsXML {
    @XmlElement(name = "Payment")
    private List<PaymentInfo> paymentList;

    public PaymentsXML() { }

    public PaymentsXML(List<PaymentInfo> paymentList) {
        this.paymentList = paymentList;
    }

    // Getters and Setters
}
```

- **`PaymentInfo`** is a **DTO** that encapsulates the payment method details.
- **`PaymentsXML`** is a wrapper class used to hold a list of **PaymentInfo** objects for XML serialization.
- These objects help transfer data between layers without exposing internal details, ensuring clean separation of concerns.
## API Integration and Configuration with Jersey 2.x for Payment Management

Here’s the relevant configuration for **Jersey 2.x dependencies** and **web.xml** setup for API integration. I’ve also provided the **Postman URL** and example **XML response**:

### Dependencies for Jersey 2.x

Add the following dependencies in your **pom.xml** for Jersey:

```xml
<!-- Jersey Client Dependency -->
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>2.25.1</version>
</dependency>

<!-- Jersey Server Dependency -->
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-server</artifactId>
    <version>2.25.1</version>
</dependency>

<!-- Jersey Container Servlet Dependency -->
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-servlet</artifactId>
    <version>2.25.1</version>
</dependency>
```

### web.xml Configuration for Jersey API

Here’s the relevant section of your `web.xml` to configure the Jersey servlet for API routing:

```xml
<!-- Jersey REST Servlet -->
    <servlet>
        <servlet-name>RestVersion3</servlet-name>
        <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
        <init-param>
            <param-name>jersey.config.server.provider.packages</param-name>
            <param-value>com.mku.resources</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- Mapping of API URL to Jersey Servlet -->
    <servlet-mapping>
        <servlet-name>RestVersion3</servlet-name>
        <url-pattern>/api/*</url-pattern>
    </servlet-mapping>
```

### Postman URL for API Call

**GET Request URL**:
```
GET http://localhost:8080/version3/api/payments
```

### Example XML Response (Status 200)

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Payments>
    <Payment>
        <paymentId>1</paymentId>
        <paymentMethod>Debit Card</paymentMethod>
        <cardNumber>522323555555211</cardNumber>
        <expiryDate>2024-07</expiryDate>
        <userId>1</userId>
    </Payment>
    .
    .
    .
</Payments>
```

### How to Test with Postman:

1. **Method**: `GET`
2. **URL**: `http://localhost:8080/version3/api/payments`
3. **Headers** (Optional):
   - `Accept`: `application/xml` (or `application/json` if you want a JSON response).

Make the request and you should see the above **XML** response with payment data.


## Summary of Layer Interactions

1. **GUI Layer** interacts with the **Business Layer** (via `PaymentService`) to retrieve and manipulate data. The **Business Layer** processes the data and returns results back to the **GUI Layer** for presentation.
   
2. **Resources Layer** (API) interacts with the **Business Layer** to provide external access to the application’s functionality, calling the **Business Layer** to process and return data.
   
3. **Business Layer** calls the **Persistence Layer** (via `Payment_CRUD`) to retrieve, update, or delete data from the database.

4. **Persistence Layer** performs database operations and returns the results (such as a **PaymentInfo** object) back to the **Business Layer** for further processing.

5. The **Helper Layer** provides **DTOs** like **`PaymentInfo`** to transfer data between layers, and facilitates **XML serialization** for APIs.

## Conclusion

In **Version 3**, the application leverages a **N-layer architecture** to achieve clean separation of concerns and improve maintainability.

 The **GUI Layer** and **Resources Layer** make calls to the **Business Layer**, which then communicates with the **Persistence Layer** to perform database operations. The **Helper Layer** aids in data transfer and serialization. This modular design enhances the scalability and flexibility of the application, making it easier to extend and maintain in the future.