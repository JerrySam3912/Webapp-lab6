package com.student.controller;

import com.student.dao.UserDAO;
import com.student.model.User;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // yêu cầu: chỉ user đã login mới đổi password
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        request.getRequestDispatcher("/views/change-password.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword     = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // 1. Validate input rỗng
        if (currentPassword == null || currentPassword.trim().isEmpty()
                || newPassword == null || newPassword.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()) {

            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/views/change-password.jsp")
                   .forward(request, response);
            return;
        }

        // 2. Kiểm tra mật khẩu hiện tại bằng BCrypt (currentUser.getPassword() đang là hash) 
        if (!BCrypt.checkpw(currentPassword, currentUser.getPassword())) {
            request.setAttribute("error", "Current password is incorrect.");
            request.getRequestDispatcher("/views/change-password.jsp")
                   .forward(request, response);
            return;
        }

        // 3. Validate độ dài mật khẩu mới
        if (newPassword.length() < 8) {
            request.setAttribute("error", "New password must be at least 8 characters.");
            request.getRequestDispatcher("/views/change-password.jsp")
                   .forward(request, response);
            return;
        }

        // 4. Xác nhận mật khẩu nhập lại
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New password and confirmation do not match.");
            request.getRequestDispatcher("/views/change-password.jsp")
                   .forward(request, response);
            return;
        }

        // 5. Hash & update DB
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        boolean updated = userDAO.updatePassword(currentUser.getId(), hashed);

        if (updated) {
            // update lại password trong object session
            currentUser.setPassword(hashed);
            session.setAttribute("user", currentUser);

            request.setAttribute("success", "Password changed successfully.");
        } else {
            request.setAttribute("error", "Failed to change password. Please try again.");
        }

        request.getRequestDispatcher("/views/change-password.jsp")
               .forward(request, response);
    }
}
