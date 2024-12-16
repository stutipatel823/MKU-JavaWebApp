package com.mku.version2;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Create a new UserInfo object for this login attempt
        UserInfo user = new UserInfo();

        // Get user info from the request and trim whitespace
        String email = request.getParameter("email").trim();
        String password = request.getParameter("password").trim();
        email = "alice@example.com";
        password = "password123";
        // Debug print the trimmed email and password
        System.out.println("Trimmed Email: " + email + ", Trimmed Password: " + password);

        // Database connection details
        String DB_URL = "jdbc:mysql://localhost:3306/mku";
        String dbUsername = "root";
        String dbPassword = "root1234";

        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Database Driver Loaded Successfully.");

            try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword)) {
                System.out.println("Connected to the database.");

                // Use LOWER() in SQL to ensure case insensitivity, if needed
                String query = "SELECT * FROM User WHERE LOWER(email) = LOWER(?) AND password = ?";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, email);
                    pst.setString(2, password);

                    // Print the query and parameters to check for correctness
                    System.out.println("Executing query with Email: " + email + " and Password: " + password);

                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("User found with provided email and password.");

                            // Populate user object with data from the database
                            user.setFirstname(rs.getString("firstname"));
                            user.setLastname(rs.getString("lastname"));
                            user.setEmail(rs.getString("email"));
                            user.setPassword(rs.getString("password"));
                            user.setPhonenumber(rs.getString("phonenumber"));
                            user.setUserId(rs.getInt("user_id"));

                            // Populate address if it exists
                            user.setStreet(rs.getString("street") != null ? rs.getString("street") : "");
                            user.setCity(rs.getString("city") != null ? rs.getString("city") : "");
                            user.setProvince(rs.getString("province") != null ? rs.getString("province") : "");
                            user.setCountry(rs.getString("country") != null ? rs.getString("country") : "");
                            user.setPostalCode(rs.getString("postalcode") != null ? rs.getString("postalcode") : "");

                            // Clear any existing user from the session
                            request.getSession().removeAttribute("LoggedInUser");

                            // Store the new user object in session
                            request.getSession().setAttribute("LoggedInUser", user);

                            // Forward to loginsuccessful.jsp
                            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/loginsuccessful.jsp");
                            dispatcher.forward(request, response);
                        } else {
                            System.out.println("No user found with provided email and password.");
                            request.getRequestDispatcher("/jsp/loginfailed.jsp").forward(request, response);
                            out.println("<h1>Invalid username or password.</h1>");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Database Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database error occurred.");
            e.printStackTrace();
        } finally {
            out.close();
        }
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
        return "Short description";
    }
}
