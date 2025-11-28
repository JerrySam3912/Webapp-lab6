<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f5f5;
        }
        
        .navbar {
            background: #2c3e50;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .navbar h2 {
            font-size: 20px;
        }
        
        .navbar-right {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .role-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .role-admin {
            background: #e74c3c;
        }
        
        .role-user {
            background: #3498db;
        }
        
        .btn-logout {
            padding: 8px 20px;
            background: #e74c3c;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 14px;
            transition: background 0.3s;
        }
        
        .btn-logout:hover {
            background: #c0392b;
        }

        /* ✅ THÊM CHO EX8: nút Change Password */
        .btn-change-password {
            padding: 8px 20px;
            background: #2980b9;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 14px;
            transition: background 0.3s;
        }
        
        .btn-change-password:hover {
            background: #1f6390;
        }
        
        .container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }
        
        .welcome-card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .welcome-card h1 {
            color: #2c3e50;
            margin-bottom: 10px;
        }
        
        .welcome-card p {
            color: #7f8c8d;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .stat-icon {
            font-size: 40px;
            width: 60px;
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 10px;
        }
        
        .stat-icon-students {
            background: #e8f4fd;
        }
        
        .stat-content h3 {
            font-size: 28px;
            color: #2c3e50;
            margin-bottom: 5px;
        }
        
        .stat-content p {
            color: #7f8c8d;
            font-size: 14px;
        }
        
        .quick-actions {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .quick-actions h2 {
            color: #2c3e50;
            margin-bottom: 20px;
        }
        
        .action-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        
        .action-btn {
            padding: 20px;
            background: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            text-align: center;
            transition: all 0.3s;
            display: block;
        }
        
        .action-btn:hover {
            background: #2980b9;
            transform: translateY(-2px);
        }
        
        .action-btn-primary {
            background: #3498db;
        }
        
        .action-btn-success {
            background: #27ae60;
        }
        
        .action-btn-warning {
            background: #f39c12;
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <div class="navbar">
        <h2>📚 Student Management System</h2>
        <div class="navbar-right">
            <div class="user-info">
                <span>${sessionScope.fullName}</span>
                <span class="role-badge role-${sessionScope.role}">
                    ${sessionScope.role}
                </span>
            </div>

            <!-- ✅ THÊM CHO EX8: link Change Password -->
            <a href="change-password" class="btn-change-password">
                Change Password
            </a>

            <a href="logout" class="btn-logout">Logout</a>
        </div>
    </div>
    
    <!-- Main Content -->
    <div class="container">
        <!-- Welcome Card -->
        <div class="welcome-card">
            <h1>${welcomeMessage}</h1>
            <p>Here's what's happening with your students today.</p>
        </div>
        
        <!-- Statistics -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon stat-icon-students">
                    🎓👨
                </div>
                <div class="stat-content">
                    <h3>${totalStudents}</h3>
                    <p>Total Students</p>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="quick-actions">
            <h2>Quick Actions</h2>
            <div class="action-grid">
                <a href="student?action=list" class="action-btn action-btn-primary">
                    📋 View All Students
                </a>
                
                <c:if test="${sessionScope.role eq 'admin'}">
                    <a href="student?action=new" class="action-btn action-btn-success">
                        ➕ Add New Student
                    </a>
                </c:if>
                
                <a href="student?action=search" class="action-btn action-btn-warning">
                    🔍 Search Students
                </a>
            </div>
        </div>
    </div>
</body>
</html>
