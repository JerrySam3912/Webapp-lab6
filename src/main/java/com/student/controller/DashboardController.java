package com.student.controller;

import com.student.dao.StudentDAO;
import com.student.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get user from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Get statistics
        int totalStudents = studentDAO.getTotalStudents();
        
        // Set attributes
        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("welcomeMessage", "Welcome back, " + user.getFullName() + "!");
        
        // Forward to dashboard
        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
    }
}
