# Version 1: Intro to JSP, Servlets, JDBC Connections

## Overview

This project demonstrates the integration of Java Servlets, JSP (JavaServer Pages), and MySQL to create a simple login system. The main objectives of this project are to understand:

1. Connecting to a database
2. Receiving values from user input and linking JSP with Servlets
3. Sending data between Servlets and JSP using sessions and request dispatchers

## Concepts Explained

### 1. Connecting to a Database

To connect to a MySQL database, the following steps were taken:

- **JDBC Driver**: The MySQL JDBC driver is loaded using `Class.forName("com.mysql.cj.jdbc.Driver")`.
- **Database URL**: The connection string specifies the database to connect to:
  ```java
  String DB_URL = "jdbc:mysql://localhost:3306/mku";
  ```
- **Credentials**: Username and password are provided for authentication.
- **Connection Establishment**: A `Connection` object is created using `DriverManager.getConnection(DB_URL, dbUsername, dbPassword)`.

### 2. Receiving Values from Input and Linking JSP with Servlets

User input is received via an HTML form, which is linked to the servlet through the form's `action` attribute. The action points to the servlet's URL pattern defined by the `@WebServlet` annotation:

```java
@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
```

The form in the JSP page looks like this:

```html
<form action="login" method="post">
    <input type="text" name="username" required>
    <input type="password" name="password" required>
    <button type="submit">Login</button>
</form>
```

In the servlet, input values are retrieved using `request.getParameter()`:

```java
String username = request.getParameter("username");
String password = request.getParameter("password");
```

### 3. Sending Data to Another JSP

After a successful login, user data is sent to another JSP page (`loginsuccessful.jsp`) using session attributes and request dispatching.

- **Storing User Data**: The `UserInfo` object is stored in the session:
  ```java
  request.getSession().setAttribute("LoggedInUser", user);
  ```

- **Forwarding to JSP**: The servlet forwards the request to the JSP using a `RequestDispatcher`:
  ```java
  RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/loginsuccessful.jsp");
  dispatcher.forward(request, response);
  ```

In the JSP, the user data is retrieved from the session and displayed:
```jsp
UserInfo user = (UserInfo) session.getAttribute("LoggedInUser");
```

### 4. What is `contextPath`?

In a Java web application, the **context path** is the base URL where your application is deployed. It helps create links that will always work, no matter where the app is hosted.

#### How is `contextPath` used?

In this project, `contextPath` is used in JSP pages to create dynamic URLs that point to the right location. For example:

```jsp
<a href="${pageContext.request.contextPath}/cart">
    <button type="button">My Cart</button>
</a>
```

- `${pageContext.request.contextPath}` generates the correct base URL for the application, making sure links work properly.

#### Why use `contextPath`?

Using `contextPath` makes your links adaptable. If the app is deployed in a different location, these links will still point to the correct page without needing to be changed.

## UserInfo Class

The `UserInfo` class is a simple JavaBean/Plain Old Java Object (POJO) that holds user details such as:

- First Name
- Last Name
- Username
- Password
- Email
- Phone Number

### Sample UserInfo Class

```java
public class UserInfo {
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private int phonenumber;

    // Getters and setters for each field
}
```

**Encapsulation**: The `UserInfo` class uses the encapsulation principle, which restricts direct access to its fields by making them private. Access to these fields is provided through public getter and setter methods, promoting data protection and abstraction.

## JSP Pages

### Login Form (`login.jsp`)

The login form collects the username and password from the user:

```html
<form action="login" method="post">
    <input type="text" name="username" required>
    <input type="password" name="password" required>
    <button type="submit">Login</button>
</form>
```

### Login Successful Page (`loginsuccessful.jsp`)

This page displays a welcome message along with the user information:

```jsp
<h1>Welcome, <%= user.getFirstname() + " " + user.getLastname() %>!</h1>
<p>Email: <%= user.getEmail() %></p>
<p>Phone Number: <%= user.getPhonenumber() %></p>
<p>Username: <%= user.getUsername() %></p>
```

## Conclusion

This project serves as a foundational learning experience for building web applications using Java Servlets, JSP, and MySQL. Understanding these concepts will help in developing more complex applications in the future.
