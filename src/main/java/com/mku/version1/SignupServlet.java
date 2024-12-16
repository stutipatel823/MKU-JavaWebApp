package com.mku.version1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SignupServlet", urlPatterns = {"/signup"})
public class SignupServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mku";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root1234";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String firstname = capitalizeFirstLetter(request.getParameter("firstname"));
        String lastname = capitalizeFirstLetter(request.getParameter("lastname"));
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!password.equals(confirmPassword)) {
                request.setAttribute("errorMessage","Couldn't Create a New User");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
                dispatcher.forward(request, response);
                return;
        }

        if (isEmailUnique(email)) {
            int userId = insertUser(firstname, lastname, email, password); // Hash the password
            if (userId != -1) {
                UserInfo user = new UserInfo(userId, firstname, lastname, email, password);
                request.getSession().setAttribute("LoggedInUser", user);

                // Forward to loginsuccessful.jsp
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/loginsuccessful.jsp");
                dispatcher.forward(request, response);

            } else {
                request.setAttribute("errorMessage","Couldn't Create a New User");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
                dispatcher.forward(request, response);
            }
        } else {
                request.setAttribute("errorMessage","Email Already In Use!");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error.jsp");
                dispatcher.forward(request, response);
        }
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private boolean isEmailUnique(String email) {
        String query = "SELECT COUNT(*) FROM User WHERE email = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) == 0; // Email is unique
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Consider using a logging framework
        }
        return false;
    }

    private int insertUser(String firstname, String lastname, String email, String password) {
        String insertQuery = "INSERT INTO User (firstname, lastname, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, firstname);
            ps.setString(2, lastname);
            ps.setString(3, email);
            ps.setString(4, password);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the generated userId
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logging framework
        }
        return -1; // Return -1 if user creation failed
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "SignupServlet for user registration.";
    }
}
