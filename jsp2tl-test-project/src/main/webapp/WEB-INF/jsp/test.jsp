<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Simple JSP Test</title>
</head>
<body>
    <h1><c:out value="Welcome to JSP Test Page" /></h1>
    
    <!-- Test de scriptlets -->
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
    
    <!-- Test JSTL -->
    <c:if test="${not empty user}">
        <p>Hello, ${user.name}!</p>
    </c:if>
    
    <c:forEach var="item" items="${items}" varStatus="status">
        <li>${status.count}. ${item.name}</li>
    </c:forEach>
</body>
</html>