package com.student.controller;

import com.student.dao.UserDAO;
import com.student.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() {
        userDAO = new UserDAO();
    }
    
    /**
     * Display login page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // If already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("dashboard");
            return;
        }
        
        // Show login page
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }
    
    /**
     * Process login form
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("remember");
        
        // Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate user
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            // Authentication successful
            
            // Invalidate old session (prevent session fixation)
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            
            // Create new session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());
            session.setAttribute("fullName", user.getFullName());
            
            // Set session timeout (30 minutes)
            session.setMaxInactiveInterval(30 * 60);
            
            // Handle "Remember Me" (optional - cookie implementation)
            if ("on".equals(rememberMe)) {
                // TODO: Implement remember me functionality with cookie
            }
            
            // Redirect based on role
            if (user.isAdmin()) {
                response.sendRedirect("dashboard");
            } else {
                response.sendRedirect("student?action=list");
            }
            
        } else {
            // Authentication failed
            request.setAttribute("error", "Invalid username or password");
            request.setAttribute("username", username); // Keep username in form
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
