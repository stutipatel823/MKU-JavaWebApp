<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mku.version1.UserInfo" %>

<%
    UserInfo user = (UserInfo) session.getAttribute("LoggedInUser");
%>
<!DOCTYPE html>
<html>

<head>
    <title> Makeup Universe </title>
    <link rel="stylesheet" type="text/css" href="/version1/css/common.css">
</head>

<body>
    <div class="container" style="justify-content: center">
        <div class="menu-container">
            <h1 class="title">Hello <%= user.getFirstname() %> <%= user.getLastname() %></h1>
            <p class="welcome-message">Welcome to Makeup Universe! Your beauty destination.</p>
            <img src="../Resources/logo.png" height="250" width="250">
            <div class="button-container">
                <a href="${pageContext.request.contextPath}/cart">
                    <button class="btn" style="background-color: pink;" type="button">View My Cart</button>
                </a>
                <a href="${pageContext.request.contextPath}/searchproducts" class="search">
                    <button class="btn" style="background-color: pink;" type="button">Search Products</button>
                </a>
            </div>

        </div>
    </div>

</body>

</html>