<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mku.helper.UserInfo" %>

<%
    String userFirstname = (String) session.getAttribute("userFirstname");
    String userLastname = (String) session.getAttribute("userLastname");
%>
<!DOCTYPE html>
<html>

<head>
    <title> Makeup Universe </title>
    <link rel="stylesheet" type="text/css" href="../css/common.css"> 
</head>

<body>
    <div class="container" style="justify-content: center">
        <div class="menu-container">
            <!--<h1 class="title">Hello <%= userFirstname %> <%= userLastname %></h1>-->
            <h1 class="title">Hello ${userFirstname} ${userLastname}</h1> <!-- no need to set these as a new variable name, just use the attributes directly by calling the attribute name -->

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