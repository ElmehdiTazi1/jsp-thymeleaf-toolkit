<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Simple JSP Test</title>
</head>
<body>
    <h1><c:out value="Welcome to JSP Test Page" /></h1>
    
    <!-- JSTL conditionals -->
    <c:if test="${not empty user}">
        <p>Hello, ${user.name}!</p>
    </c:if>
    
    <c:choose>
        <c:when test="${userType == 'admin'}">
            <p>Welcome Administrator</p>
        </c:when>
        <c:when test="${userType == 'user'}">
            <p>Welcome Regular User</p>
        </c:when>
        <c:otherwise>
            <p>Welcome Guest</p>
        </c:otherwise>
    </c:choose>
    
    <!-- JSTL iteration -->
    <h2>List of Items:</h2>
    <ul>
        <c:forEach var="item" items="${items}" varStatus="status">
            <li>${status.count}. ${item.name} - $${item.price}</li>
        </c:forEach>
    </ul>
    
    <!-- Scriptlet examples to test conversion -->
    <%
        String serverTime = new java.util.Date().toString();
        out.println("<p>Current server time is: " + serverTime + "</p>");
        
        int value = 10;
        if (value > 5) {
            out.println("<p>Value is greater than 5</p>");
        }
    %>
    
    <% for (int i = 0; i < 3; i++) { %>
        <div>Dynamic content <%= i+1 %></div>
    <% } %>
    
    <!-- Spring Form Example -->
    <form:form action="/submit" modelAttribute="userForm">
        <div>
            <form:label path="username">Username:</form:label>
            <form:input path="username" />
            <form:errors path="username" cssClass="error" />
        </div>
        <div>
            <form:label path="email">Email:</form:label>
            <form:input path="email" />
            <form:errors path="email" cssClass="error" />
        </div>
        <div>
            <form:label path="role">Role:</form:label>
            <form:select path="role">
                <form:option value="" label="-- Select Role --" />
                <form:options items="${availableRoles}" />
            </form:select>
        </div>
        <div>
            <form:checkbox path="active" label="Active Account" />
        </div>
        <div>
            <button type="submit">Submit</button>
        </div>
    </form:form>
</body>
</html>