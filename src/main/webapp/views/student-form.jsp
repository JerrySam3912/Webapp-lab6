<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${student != null && param.action != 'insert'}">Edit Student</c:when>
            <c:otherwise>Add New Student</c:otherwise>
        </c:choose>
    </title>
    
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex; justify-content: center;
            align-items: center; padding: 20px;
        }
        .container {
            background: white; border-radius: 10px;
            padding: 40px; box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            width: 100%; max-width: 600px;
        }
        h1 { color: #333; margin-bottom: 30px; font-size: 28px; text-align: center; }
        .form-group { margin-bottom: 25px; }
        label { display: block; margin-bottom: 8px; color: #555; font-weight: 500; font-size: 14px; }
        input[type="text"], input[type="email"], input[type="file"], select {
            width: 100%; padding: 12px 15px; border: 2px solid #ddd;
            border-radius: 5px; font-size: 14px; transition: border-color 0.3s;
        }
        input:focus, select:focus { outline: none; border-color: #667eea; }
        .required { color: #dc3545; }
        .button-group { display: flex; gap: 15px; margin-top: 30px; }
        .btn {
            flex: 1; padding: 14px; border: none; border-radius: 5px;
            font-size: 16px; font-weight: 600; cursor: pointer;
            transition: all 0.3s; text-decoration: none; text-align: center;
        }
        .btn-primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
        .btn-primary:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(102,126,234,0.4); }
        .btn-secondary { background-color: #6c757d; color: white; }
        .btn-secondary:hover { background-color: #5a6268; }
        .info-text { font-size: 12px; color: #666; margin-top: 5px; }
        .error { color: red; font-size: 14px; display: block; margin-top: 5px; }
    </style>
</head>

<body>
    <div class="container">
        <h1>
            <c:choose>
                <c:when test="${student != null && param.action != 'insert'}">✏️ Edit Student</c:when>
                <c:otherwise>➕ Add New Student</c:otherwise>
            </c:choose>
        </h1>
        
        <!-- BONUS 2 HERE: enctype added -->
        <form action="student" method="POST" enctype="multipart/form-data">

            <input type="hidden" name="action" 
                   value="${student != null && param.action != 'insert' ? 'update' : 'insert'}">

            <c:if test="${student != null}">
                <input type="hidden" name="id" value="${student.id}">
            </c:if>

            <!-- Student Code -->
            <div class="form-group">
                <label for="studentCode">Student Code <span class="required">*</span></label>
                <input type="text" id="studentCode" name="studentCode"
                       value="${student.studentCode}"
                       ${student != null ? 'readonly' : ''}
                       placeholder="e.g., SV001, IT123">

                <p class="info-text">Format: 2 letters + 3+ digits</p>

                <c:if test="${not empty errorCode}">
                    <span class="error">${errorCode}</span>
                </c:if>
            </div>

            <!-- Full Name -->
            <div class="form-group">
                <label for="fullName">Full Name <span class="required">*</span></label>
                <input type="text" id="fullName" name="fullName"
                       value="${student.fullName}" placeholder="Enter full name">
                <c:if test="${not empty errorName}">
                    <span class="error">${errorName}</span>
                </c:if>
            </div>

            <!-- Email -->
            <div class="form-group">
                <label for="email">Email <span class="required">*</span></label>
                <input type="text" id="email" name="email"
                       value="${student.email}" placeholder="student@example.com">
                <c:if test="${not empty errorEmail}">
                    <span class="error">${errorEmail}</span>
                </c:if>
            </div>

            <!-- Major -->
            <div class="form-group">
                <label for="major">Major <span class="required">*</span></label>
                <select id="major" name="major">
                    <option value="">-- Select Major --</option>
                    <option value="Computer Science" ${student.major == 'Computer Science' ? 'selected' : ''}>Computer Science</option>
                    <option value="Information Technology" ${student.major == 'Information Technology' ? 'selected' : ''}>Information Technology</option>
                    <option value="Software Engineering" ${student.major == 'Software Engineering' ? 'selected' : ''}>Software Engineering</option>
                    <option value="Business Administration" ${student.major == 'Business Administration' ? 'selected' : ''}>Business Administration</option>
                </select>
                <c:if test="${not empty errorMajor}">
                    <span class="error">${errorMajor}</span>
                </c:if>
            </div>

            <!-- BONUS 2: Upload Photo -->
            <div class="form-group">
                <label for="photo">Photo (optional)</label>
                <input type="file" id="photo" name="photo" accept="image/*">

                <c:if test="${not empty errorPhoto}">
                    <span class="error">${errorPhoto}</span>
                </c:if>

                <!-- preview ảnh khi edit -->
                <c:if test="${student != null && student.photo != null}">
                    <div style="margin-top: 10px;">
                        <img src="${pageContext.request.contextPath}/uploads/${student.photo}"
                             width="100" height="100"
                             style="object-fit: cover; border-radius: 6px;">
                    </div>
                </c:if>
            </div>

            <!-- Buttons -->
            <div class="button-group">
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${student != null}">💾 Update Student</c:when>
                        <c:otherwise>➕ Add Student</c:otherwise>
                    </c:choose>
                </button>
                <a href="student?action=list" class="btn btn-secondary">❌ Cancel</a>
            </div>

        </form>
    </div>
</body>
</html>
