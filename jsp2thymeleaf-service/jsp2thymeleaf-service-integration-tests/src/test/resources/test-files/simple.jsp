<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test JSP Page</title>
</head>
<body>
    <h1>Welcome ${user.name}</h1>
    
    <c:if test="${not empty items}">
        <ul>
            <c:forEach var="item" items="${items}">
                <li>${item.name} - ${item.description}</li>
            </c:forEach>
        </ul>
    </c:if>
    
    <c:choose>
        <c:when test="${user.admin}">
            <div>Admin Panel</div>
        </c:when>
        <c:otherwise>
            <div>User Panel</div>
        </c:otherwise>
    </c:choose>
</body>
</html>
