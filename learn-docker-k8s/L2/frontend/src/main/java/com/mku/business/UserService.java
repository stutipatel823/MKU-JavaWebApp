package com.mku.business;

import com.mku.helper.UserInfo;
import com.mku.persistence.User_CRUD;
import java.sql.SQLException;

/**
 * Business logic layer for handling user operations.
 */
public class UserService {

    /**
     * Registers a new user in the system.
     * 
     * @param userInfo The user information.
     * @param confirmPassword The confirmation password for validation.
     * @throws SQLException if user registration fails.
     */
    public int registerUser(String firstname, String lastname, String email, String password, String confirmPassword) throws SQLException {
        // Validate the user details
        if (!password.equals(confirmPassword)) {
            throw new SQLException("Passwords do not match.");
        }

        // Check if the email already exists
        if (!User_CRUD.isEmailUnique(email)) {
            throw new SQLException("Email is already in use.");
        }

        // Call the CRUD method to insert the user into the database
        return User_CRUD.createUser(firstname, lastname, email, password, confirmPassword);
    }

    /**
     * Authenticates the user with the provided credentials (email and password).
     * 
     * @param email The user's email.
     * @param password The user's password.
     * @return A UserInfo object if authentication is successful, null otherwise.
     * @throws SQLException if authentication fails.
     */
    public UserInfo authenticateUser(String email, String password) throws SQLException {
        // Call the CRUD method to retrieve the user from the database
        return User_CRUD.readUser(email, password);
    }
}
