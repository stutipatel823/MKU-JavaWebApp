<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Makeup Universe - Sign Up</title>
    <link rel="stylesheet" href="./css/common.css">
    <link rel="stylesheet" href="./css/login_signup.css">

</head>

<body>
    <div class="container">
        <div class="menu-container">
            <img src="../Resources/logo.png" alt="Logo" class="logo">
            <form class="form-container" action="${pageContext.request.contextPath}/signup" method="post">
                <h1 class="form-title">Sign Up</h1>
                <label class="form-label">First Name:</label>
                <input type="text" placeholder="First Name" name="firstname" required class="form-input">

                <label class="form-label">Last Name:</label>
                <input type="text" placeholder="Last Name" name="lastname" required class="form-input">

                <label class="form-label">Email:</label>
                <input type="email" placeholder="Email" name="email" required class="form-input">

                <label class="form-label">Password:</label>
                <input type="password" placeholder="Password" name="password" required class="form-input">

                <label class="form-label">Confirm Password:</label>
                <input type="password" placeholder="Confirm Password" name="confirmPassword" required
                    class="form-input">

                <button type="submit" class="btn" style="background-color:#fadc83;">Sign Up</button>
                <p class="form-footer">Already a Member? <a href="${pageContext.request.contextPath}/">Login</a></p>
            </form>
        </div>
    </div>

</body>

</html>