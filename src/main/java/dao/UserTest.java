package dao;

import model.User;

public class UserTest {

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        // Test 1: Register a new user
        System.out.println("--- Test 1: Register a new user ---");
        User newUser = new User(0, "testuser", "password123", "testuser@example.com");
        try {
            boolean registered = userDAO.register(newUser);
            if (registered) {
                System.out.println("Registration successful!");
            } else {
                System.out.println("Registration failed: email already exists.");
            }
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
        }

        // Test 2: Login with correct credentials
        System.out.println("\n--- Test 2: Login with correct credentials ---");
        try {
            User loggedInUser = userDAO.login("testuser@example.com", "password123");
            if (loggedInUser != null) {
                System.out.println("Login successful! Welcome, " + loggedInUser.getUsername());
            } else {
                System.out.println("Login failed: invalid credentials.");
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }

        // Test 3: Login with incorrect credentials
        System.out.println("\n--- Test 3: Login with incorrect credentials ---");
        try {
            User loggedInUser = userDAO.login("testuser@example.com", "wrongpassword");
            if (loggedInUser != null) {
                System.out.println("Login successful! Welcome, " + loggedInUser.getUsername());
            } else {
                System.out.println("Login failed: invalid credentials.");
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }
}
