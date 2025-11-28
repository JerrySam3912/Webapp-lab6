<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Change Password</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet"/>
</head>
<body class="bg-light">

<div class="container mt-5" style="max-width: 500px;">
    <h2 class="mb-4 text-center">Change Password</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <form action="change-password" method="post">
        <div class="mb-3">
            <label class="form-label">Current Password</label>
            <input type="password" name="currentPassword" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">New Password</label>
            <input type="password" name="newPassword" class="form-control" required>
            <div class="form-text">Minimum 8 characters.</div>
        </div>

        <div class="mb-3">
            <label class="form-label">Confirm New Password</label>
            <input type="password" name="confirmPassword" class="form-control" required>
        </div>

        <button type="submit" class="btn btn-primary w-100">Change Password</button>
        <a href="dashboard" class="btn btn-link w-100 mt-2">Back to Dashboard</a>
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
