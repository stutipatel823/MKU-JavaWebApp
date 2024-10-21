<%-- 
    Document   : signup
    Created on : Jan. 20, 2023, 6:23:50 p.m.
    Author     : stutipatel
--%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Makeup Universe - Sign Up</title>
    <link rel="stylesheet" href="../css/common.css">
</head>

<body>
    <div class="form-container form-container--signup">
        <img src="../Resources/logo.png" alt="Logo" class="logo">
        <form action="signup" method="post">
            <h1 class="form-title">Sign Up</h1>
            <label class="form-label">Username:</label>
            <input type="text" placeholder="Username" name="username" required class="form-input">

            <label class="form-label">Email:</label>
            <input type="email" placeholder="Email" name="email" required class="form-input">

            <label class="form-label">Password:</label>
            <input type="password" placeholder="Password" name="password" required class="form-input">

            <label class="form-label">Confirm Password:</label>
            <input type="password" placeholder="Confirm Password" name="confirmPassword" required class="form-input">

            <button type="submit" class="btn btn--signup">Sign Up</button>
            <p class="form-footer">Already a Member? <a href="../index.html">Login</a></p>
        </form>
    </div>
</body>

</html>