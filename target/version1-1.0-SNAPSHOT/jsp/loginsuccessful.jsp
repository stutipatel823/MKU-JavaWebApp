<%-- 
    Document   : loginsuccessful
    Created on : Jan. 20, 2023, 6:23:50 p.m.
    Author     : stutipatel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mku.version1.UserInfo" %>

<%
    UserInfo user = (UserInfo) session.getAttribute("LoggedInUser");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Successful</title>
    </head>
    <body>
        <h1>Welcome, <%= user.getFirstname()+ " "+ user.getLastname()%>!</h1>
        <p>Email: <%= user.getEmail()%></p>
        <p>Phone Number: <%= user.getPhonenumber()%></p>
        <p>Username: <%= user.getUsername()%></p>
        <p>Password: <%= user.getPassword()%></p>
    </body>
</html>
