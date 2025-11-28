<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List - MVC</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh; padding: 20px;
        }
        .container {
            max-width: 1200px; margin: 0 auto; background: white;
            border-radius: 10px; padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        h1 { color: #333; margin-bottom: 10px; font-size: 32px; }
        .subtitle { color: #666; margin-bottom: 30px; font-style: italic; }
        .message {
            padding: 15px; margin-bottom: 20px; border-radius: 5px; font-weight: 500;
        }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .btn {
            display: inline-block; padding: 12px 24px; text-decoration: none;
            border-radius: 5px; font-weight: 500; transition: all 0.3s;
            border: none; cursor: pointer; font-size: 14px;
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .btn-secondary { background-color: #6c757d; color: white; }
        .btn-danger {
            background-color: #dc3545; color: white;
            padding: 8px 16px; font-size: 13px;
        }
        .btn-danger:hover { background-color: #c82333; }
        table {
            width: 100%; border-collapse: collapse; margin-top: 20px;
        }
        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        th, td {
            padding: 15px; text-align: left; border-bottom: 1px solid #ddd;
        }
        th {
            font-weight: 600; text-transform: uppercase;
            font-size: 13px; letter-spacing: 0.5px;
        }
        tbody tr { transition: background-color 0.2s; }
        tbody tr:hover { background-color: #f8f9fa; }
        .actions { display: flex; gap: 10px; }
        .empty-state { text-align: center; padding: 60px 20px; color: #999; }
        .empty-state-icon { font-size: 64px; margin-bottom: 20px; }

        .pagination {
            margin: 25px 0; text-align: center;
        }
        .pagination a, .pagination strong {
            padding: 8px 14px; margin: 0 4px;
            border-radius: 5px; border: 1px solid #ddd;
            text-decoration: none; font-size: 14px;
            color: #333; background-color: #f8f9fa;
        }
        .pagination a:hover { background-color: #e2e6ea; }
        .pagination strong {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; border: none;
        }

        .student-photo {
            width: 50px; height: 50px;
            object-fit: cover; border-radius: 4px;
        }

        /* ========== EX7: NAVBAR + ROLE BADGE ========== */
        .navbar {
            max-width: 1200px;
            margin: 0 auto 20px auto;
            padding: 12px 20px;
            background: rgba(255,255,255,0.95);
            border-radius: 10px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .navbar h2 {
            font-size: 20px;
            color: #333;
        }
        .navbar-right {
            display: flex;
            align-items: center;
            gap: 16px;
        }
        .user-info {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            font-size: 14px;
        }
        .user-info span:first-child {
            margin-bottom: 4px;
        }
        .role-badge {
            padding: 3px 10px;
            border-radius: 999px;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .role-admin {
            background-color: #ffe3e3;
            color: #b71c1c;
        }
        .role-user {
            background-color: #e3f2fd;
            color: #1565c0;
        }
        .btn-nav,
        .btn-logout {
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 5px;
            border: 1px solid #ddd;
            font-size: 13px;
            color: #333;
            background-color: #f8f9fa;
            transition: all 0.2s;
        }
        .btn-nav:hover,
        .btn-logout:hover {
            background-color: #e2e6ea;
        }
    </style>
</head>
<body>

    <!-- EX7: Navigation Bar -->
    <div class="navbar">
        <h2>📚 Student Management System</h2>
        <div class="navbar-right">
            <div class="user-info">
                <span>Welcome, ${sessionScope.fullName}</span>
                <span class="role-badge role-${sessionScope.role}">
                    ${sessionScope.role}
                </span>
            </div>
            <a href="dashboard" class="btn-nav">Dashboard</a>
            <a href="logout" class="btn-logout">Logout</a>
        </div>
    </div>

    <div class="container">
        <h1>📚 Student Management System</h1>
        <p class="subtitle">MVC Pattern with Jakarta EE & JSTL</p>
        
        <!-- Success Message -->
        <c:if test="${not empty param.message}">
            <div class="message success">
                ✅ ${param.message}
            </div>
        </c:if>
        
        <!-- Error Message -->
        <c:if test="${not empty param.error}">
            <div class="message error">
                ❌ ${param.error}
            </div>
        </c:if>
        
        <!-- Add New Student Button (Admin only) -->
        <div style="margin-bottom: 20px;">
            <c:if test="${sessionScope.role eq 'admin'}">
                <a href="student?action=new" class="btn btn-primary">
                    ➕ Add New Student
                </a>
            </c:if>

            <!-- BONUS 1: Export to Excel -->
            <a href="${pageContext.request.contextPath}/export"
               class="btn btn-secondary"
               style="margin-left: 10px;">
                ⬇ Export to Excel
            </a>
        </div>
        
        <!-- Search Form (BONUS 3: dùng action=list + giữ major/sortBy/order) -->
        <form action="student" method="GET"
              style="display: flex; gap: 8px; align-items: center; margin-bottom: 10px;">
            <input type="hidden" name="action" value="list">
            <!-- giữ filter & sort -->
            <input type="hidden" name="major" value="${selectedMajor}">
            <input type="hidden" name="sortBy" value="${sortBy}">
            <input type="hidden" name="order" value="${order}">

            <input
                type="text"
                name="keyword"
                placeholder="Search by code, name, or email"
                value="${keyword}"
                style="padding: 6px 10px; border-radius: 4px; border: 1px solid #ccc; min-width: 220px;"
            >
            <button type="submit" class="btn btn-secondary">
                🔍 Search
            </button>
        </form>

        <!-- Search status + Clear button -->
        <c:if test="${not empty keyword}">
            <div class="message info"
                 style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
                <span>
                    Search results for: 
                    <strong><c:out value="${keyword}"/></strong>
                </span>
                <a href="student?action=list" class="btn btn-secondary"
                   style="background-color: #e2e6ea; color: #333;">
                    Clear
                </a>
            </div>
        </c:if>
        
        <!-- Filter by Major (BONUS 3: action=list + giữ keyword/sort) -->
        <div class="filter-box" style="margin-bottom: 15px;">
            <form action="student" method="get"
                  style="display: flex; align-items: center; gap: 10px;">
                <input type="hidden" name="action" value="list">
                <!-- giữ search & sort -->
                <input type="hidden" name="keyword" value="${keyword}">
                <input type="hidden" name="sortBy" value="${sortBy}">
                <input type="hidden" name="order" value="${order}">

                <label for="majorFilter">Filter by Major:</label>
                <select id="majorFilter" name="major">
                    <option value="">All Majors</option>
                    <option value="Computer Science" 
                            ${selectedMajor == 'Computer Science' ? 'selected' : ''}>
                        Computer Science
                    </option>
                    <option value="Information Technology" 
                            ${selectedMajor == 'Information Technology' ? 'selected' : ''}>
                        Information Technology
                    </option>
                    <option value="Software Engineering" 
                            ${selectedMajor == 'Software Engineering' ? 'selected' : ''}>
                        Software Engineering
                    </option>
                    <option value="Business Administration" 
                            ${selectedMajor == 'Business Administration' ? 'selected' : ''}>
                        Business Administration
                    </option>
                </select>
                <button type="submit" class="btn btn-secondary">Apply Filter</button>

                <c:if test="${not empty selectedMajor}">
                    <a href="student?action=list" class="btn btn-secondary" 
                       style="margin-left: 8px; background-color: #e2e6ea; color: #333;">
                        Clear Filter
                    </a>
                </c:if>
            </form>
        </div>

        <!-- Student Table -->
        <c:choose>
            <c:when test="${not empty students}">
                <table>
                    <thead>
                        <tr>
                            <!-- EX7 + BONUS 3: Sortable headers dùng action=list, giữ keyword/major -->
                            <th>
                                <a 
                                    href="student?action=list&sortBy=id&order=${sortBy == 'id' and order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&major=${selectedMajor}"
                                    style="color: inherit; text-decoration: none;">
                                    ID
                                    <c:if test="${sortBy == 'id'}">
                                        ${order == 'asc' ? '▲' : '▼'}
                                    </c:if>
                                </a>
                            </th>
                            <th>
                                <a 
                                    href="student?action=list&sortBy=student_code&order=${sortBy == 'student_code' and order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&major=${selectedMajor}"
                                    style="color: inherit; text-decoration: none;">
                                    Student Code
                                    <c:if test="${sortBy == 'student_code'}">
                                        ${order == 'asc' ? '▲' : '▼'}
                                    </c:if>
                                </a>
                            </th>
                            <th>
                                <a 
                                    href="student?action=list&sortBy=full_name&order=${sortBy == 'full_name' and order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&major=${selectedMajor}"
                                    style="color: inherit; text-decoration: none;">
                                    Full Name
                                    <c:if test="${sortBy == 'full_name'}">
                                        ${order == 'asc' ? '▲' : '▼'}
                                    </c:if>
                                </a>
                            </th>
                            <th>
                                <a 
                                    href="student?action=list&sortBy=email&order=${sortBy == 'email' and order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&major=${selectedMajor}"
                                    style="color: inherit; text-decoration: none;">
                                    Email
                                    <c:if test="${sortBy == 'email'}">
                                        ${order == 'asc' ? '▲' : '▼'}
                                    </c:if>
                                </a>
                            </th>
                            <th>
                                <a 
                                    href="student?action=list&sortBy=major&order=${sortBy == 'major' and order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&major=${selectedMajor}"
                                    style="color: inherit; text-decoration: none;">
                                    Major
                                    <c:if test="${sortBy == 'major'}">
                                        ${order == 'asc' ? '▲' : '▼'}
                                    </c:if>
                                </a>
                            </th>
                            <th>Photo</th>

                            <!-- EX7: Actions column only for admin -->
                            <c:if test="${sessionScope.role eq 'admin'}">
                                <th>Actions</th>
                            </c:if>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="student" items="${students}">
                            <tr>
                                <td>${student.id}</td>
                                <td><strong>${student.studentCode}</strong></td>
                                <td>${student.fullName}</td>
                                <td>${student.email}</td>
                                <td>${student.major}</td>
                                <td>
                                    <c:if test="${not empty student.photo}">
                                        <img src="${pageContext.request.contextPath}/uploads/${student.photo}" 
                                             alt="Photo"
                                             class="student-photo" />
                                    </c:if>
                                </td>

                                <!-- EX7: Action buttons - Admin only -->
                                <c:if test="${sessionScope.role eq 'admin'}">
                                    <td>
                                        <div class="actions">
                                            <a href="student?action=edit&id=${student.id}" class="btn btn-secondary">
                                                ✏️ Edit
                                            </a>
                                            <a href="student?action=delete&id=${student.id}" 
                                                class="btn btn-danger"
                                                onclick="return confirm('Are you sure you want to delete this student?')">
                                                🗑️ Delete
                                            </a>
                                        </div>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📭</div>
                    <h3>No students found</h3>
                    <p>Start by adding a new student</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
                        
    <!-- ==================== PAGINATION (EX8) ==================== -->
    <div class="pagination">

        <!-- Previous Button -->
        <c:if test="${currentPage > 1}">
            <a href="student?action=list&page=${currentPage - 1}">« Previous</a>
        </c:if>

        <!-- Page Numbers -->
        <c:forEach begin="1" end="${totalPages}" var="i">
            <c:choose>
                <c:when test="${i == currentPage}">
                    <strong>${i}</strong>
                </c:when>
                <c:otherwise>
                    <a href="student?action=list&page=${i}">${i}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <!-- Next Button -->
        <c:if test="${currentPage < totalPages}">
            <a href="student?action=list&page=${currentPage + 1}">Next »</a>
        </c:if>

    </div>

    <p style="text-align:center; margin-top:10px; color:#555;">
        Showing page <strong>${currentPage}</strong> of <strong>${totalPages}</strong>
    </p>
</body>
</html>
