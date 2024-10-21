package com.mku.version1;

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
    UserInfo user = new UserInfo();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Initialize response content type
        response.setContentType("text/html;charset=UTF-8");

        // 1. Get user info
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 2. Connect to database and query user
        String DB_URL = "jdbc:mysql://localhost:3306/mku";
        String dbUsername = "root";
        String dbPassword = "root1234";

        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword)) {
                // 3. Prepare query to see if username and password are correct
                String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, username);
                    pst.setString(2, password);
                    
                    // 4. Process query 
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            // Populate user object with data from the database
                            user.setFirstname(rs.getString("first_name"));
                            user.setLastname(rs.getString("last_name"));
                            user.setUsername(rs.getString("username"));
                            user.setEmail(rs.getString("email"));
                            user.setPhonenumber(rs.getInt("phone_number")); // Assuming phone_number is an int
                            user.setPassword(rs.getString("password"));

                            // Store user object in session
                            request.getSession().setAttribute("LoggedInUser", user);

                            // Forward to loginsuccessful.jsp
                            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/loginsuccessful.jsp");
                            dispatcher.forward(request, response);
                        }else {// else for username password incorrect
                            // Redirect to loginfailed.jsp if login fails
                            request.getRequestDispatcher("/jsp/loginfailed.jsp").forward(request, response);
                            out.println("<h1>Invalid username or password.</h1>");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ServletException("Database Driver not found.", e);
        } catch (SQLException e) {
            throw new ServletException("Database error occurred.", e);
        } finally {
            out.close(); // Ensure the writer is closed
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
