<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link rel="stylesheet" type="text/css" href="../css/common.css">
</head>
<body>
    <div class="container">
        <h1 class="title">Error</h1>
        <p><%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "An unexpected error occurred." %></p>
        <a href="${pageContext.request.contextPath}/">Go back</a>
    </div>
   
</body>
</html>
