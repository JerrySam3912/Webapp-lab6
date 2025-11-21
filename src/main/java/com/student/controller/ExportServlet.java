package com.student.controller;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.List;

@WebServlet("/export")
public class ExportServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy danh sách sinh viên
        List<Student> students = studentDAO.getAllStudents();

        // Tạo workbook Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Tạo header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Student Code");
        header.createCell(2).setCellValue("Full Name");
        header.createCell(3).setCellValue("Email");
        header.createCell(4).setCellValue("Major");

        // Populate dữ liệu
        int rowNum = 1;
        for (Student s : students) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getId());
            row.createCell(1).setCellValue(s.getStudentCode());
            row.createCell(2).setCellValue(s.getFullName());
            row.createCell(3).setCellValue(s.getEmail());
            row.createCell(4).setCellValue(s.getMajor());
        }

        // Resize cột
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }

        // Header tải file xuống
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=students.xlsx"
        );

        // Ghi file
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
