package com.student.controller;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@WebServlet("/student")
@MultipartConfig          // BONUS 2: cho phép multipart/form-data (upload file)
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "search":
                searchStudents(request, response);
                break;
            case "sort":
                sortStudents(request, response);
                break;
            case "filter":
                filterStudents(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
        }
    }
    
    
    // List all students (EX8: pagination + BONUS 3: combined search/filter/sort)
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // ===== BONUS 3: đọc các tham số kết hợp từ URL =====
        String keyword = request.getParameter("keyword");
        String major = request.getParameter("major");
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");

        boolean hasFilter = (keyword != null && !keyword.trim().isEmpty())
                || (major != null && !major.trim().isEmpty())
                || (sortBy != null && !sortBy.trim().isEmpty());

        // Nếu có search/filter/sort → dùng DAO động (BONUS 3)
        if (hasFilter) {
            List<Student> students = studentDAO.findStudents(keyword, major, sortBy, order);

            request.setAttribute("students", students);
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedMajor", major);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("order", order);

            // để tránh JSP lỗi null khi render pagination
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // ===== PHẦN DƯỚI: GIỮ NGUYÊN LOGIC EX8 PHÂN TRANG =====

        // 1. Lấy tham số page (mặc định = 1)
        String pageParam = request.getParameter("page");
        int currentPage = 1;

        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1; // nếu lỗi parse thì quay về page 1
            }
        }

        // 2. Số bản ghi mỗi trang
        int recordsPerPage = 10;

        // 3. Lấy tổng số bản ghi
        int totalRecords = studentDAO.getTotalStudents();

        // 4. Tính tổng số trang
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        if (totalPages == 0) {
            totalPages = 1; // nếu chưa có dữ liệu, tránh totalPages = 0
        }

        // 5. Xử lý edge cases: page < 1 hoặc > totalPages
        if (currentPage < 1) {
            currentPage = 1;
        } else if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        // 6. Tính offset
        int offset = (currentPage - 1) * recordsPerPage;

        // 7. Lấy danh sách sinh viên theo trang
        List<Student> students = studentDAO.getStudentsPaginated(offset, recordsPerPage);

        // 8. Gửi dữ liệu cho view
        request.setAttribute("students", students);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        // 9. Forward đến JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for new student
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for editing student
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        
        request.setAttribute("student", existingStudent);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    
    // -------------------- EX6 VALIDATION INSERTION START --------------------
    
    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;

        // Validate Student Code
        String code = student.getStudentCode();
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
            isValid = false;
        } else {
            String codePattern = "[A-Z]{2}[0-9]{3,}";
            if (!code.matches(codePattern)) {
                request.setAttribute("errorCode",
                        "Invalid format. Use 2 uppercase letters + 3+ digits (e.g., SV001)");
                isValid = false;
            }
        }

        // Validate Full Name
        String fullName = student.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else if (fullName.trim().length() < 2) {
            request.setAttribute("errorName", "Full name must be at least 2 characters");
            isValid = false;
        }

        // Validate Email (optional)
        String email = student.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!email.matches(emailPattern)) {
                request.setAttribute("errorEmail", "Invalid email format");
                isValid = false;
            }
        }

        // Validate Major
        String major = student.getMajor();
        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required");
            isValid = false;
        }

        return isValid;
    }

    // -------------------- EX6 VALIDATION INSERTION END --------------------
    
    
    // Insert new student  (BONUS 2: thêm xử lý upload ảnh)
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student(studentCode, fullName, email, major);

        // EX6: Validate before insert
        if (!validateStudent(student, request)) {
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // BONUS 2: xử lý file ảnh upload
        Part photoPart = request.getPart("photo");
        String photoFileName = null;

        if (photoPart != null && photoPart.getSize() > 0) {
            String submittedFileName = Paths.get(photoPart.getSubmittedFileName())
                                            .getFileName().toString();

            // Lấy extension
            String ext = "";
            int dotIndex = submittedFileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = submittedFileName.substring(dotIndex).toLowerCase();
            }

            // Chỉ cho phép PNG/JPG/JPEG
            if (!ext.equals(".png") && !ext.equals(".jpg") && !ext.equals(".jpeg")) {
                request.setAttribute("errorPhoto", "Only PNG/JPG images are allowed");
                request.setAttribute("student", student);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Giới hạn kích thước file, ví dụ 5MB
            long maxSize = 5L * 1024 * 1024;
            if (photoPart.getSize() > maxSize) {
                request.setAttribute("errorPhoto", "Image too large (max 5MB)");
                request.setAttribute("student", student);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Tạo tên file unique
            String uniqueName = UUID.randomUUID().toString() + ext;

            // Thư mục /uploads dưới webapp
            String uploadDir = getServletContext().getRealPath("/uploads");
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Lưu file lên ổ đĩa
            photoPart.write(uploadDir + File.separator + uniqueName);
            photoFileName = uniqueName;
        }

        // Lưu tên file vào model để DAO insert
        student.setPhoto(photoFileName);
        
        if (studentDAO.addStudent(student)) {
            response.sendRedirect("student?action=list&message=Student added successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to add student");
        }
    }
    
    // Update student (BONUS 2: cho phép đổi ảnh, giữ ảnh cũ nếu không upload mới)
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student(studentCode, fullName, email, major);
        student.setId(id);

        // BONUS 2: xử lý upload ảnh
        Part photoPart = request.getPart("photo");
        String photoFileName;

        if (photoPart != null && photoPart.getSize() > 0) {
            String submittedFileName = Paths.get(photoPart.getSubmittedFileName())
                                            .getFileName().toString();

            String ext = "";
            int dotIndex = submittedFileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = submittedFileName.substring(dotIndex).toLowerCase();
            }

            if (!ext.equals(".png") && !ext.equals(".jpg") && !ext.equals(".jpeg")) {
                request.setAttribute("errorPhoto", "Only PNG/JPG images are allowed");
                request.setAttribute("student", student);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
                dispatcher.forward(request, response);
                return;
            }

            long maxSize = 5L * 1024 * 1024;
            if (photoPart.getSize() > maxSize) {
                request.setAttribute("errorPhoto", "Image too large (max 5MB)");
                request.setAttribute("student", student);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
                dispatcher.forward(request, response);
                return;
            }

            String uniqueName = UUID.randomUUID().toString() + ext;

            String uploadDir = getServletContext().getRealPath("/uploads");
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            photoPart.write(uploadDir + File.separator + uniqueName);
            photoFileName = uniqueName;

        } else {
            // Không upload mới → lấy lại ảnh cũ từ DB
            Student existing = studentDAO.getStudentById(id);
            photoFileName = (existing != null) ? existing.getPhoto() : null;
        }

        student.setPhoto(photoFileName);

        // EX6: Validate before update
        if (!validateStudent(student, request)) {
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        if (studentDAO.updateStudent(student)) {
            response.sendRedirect("student?action=list&message=Student updated successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to update student");
        }
    }
    
    // Delete student
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }

    // Search (EX5 cũ – vẫn giữ nguyên)
    private void searchStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");

        List<Student> students;

        if (keyword == null || keyword.trim().isEmpty()) {
            students = studentDAO.getAllStudents();
        } else {
            students = studentDAO.searchStudents(keyword.trim());
        }

        request.setAttribute("students", students);
        request.setAttribute("keyword", keyword);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // -------- EX7: Sort --------
    private void sortStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");

        List<Student> students = studentDAO.getStudentsSorted(sortBy, order);

        request.setAttribute("students", students);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("order", order);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // -------- EX7: Filter by major --------
    private void filterStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String major = request.getParameter("major");
        List<Student> students;

        if (major == null || major.trim().isEmpty()) {
            students = studentDAO.getAllStudents();
        } else {
            students = studentDAO.getStudentsByMajor(major.trim());
        }

        request.setAttribute("students", students);
        request.setAttribute("selectedMajor", major);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
}
