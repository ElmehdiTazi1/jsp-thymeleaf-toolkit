<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test JSP File</title>
</head>
<body>
    <h1>Test JSP for Conversion</h1>
    
    <h3>c:url Example</h3>
    Escaped urls adjusted with the current application context.<br/>
    <pre>
    &lt;c:url value="/resources/text.txt" var="url"/&gt;
    &lt;spring:url value="/resources/text.txt" htmlEscape="true" var="springUrl" /&gt;
    </pre>
    <c:url value="/resources/text.txt" var="url"/>
    <spring:url value="/resources/text.txt" htmlEscape="true" var="springUrl" />
    
    <p>Spring URL: ${springUrl}</p>
    <p>JSTL URL: ${url}</p>
    
    <h3>c:if Example</h3>
    <c:if test="${true}">
        <p>This text will always appear.</p>
    </c:if>
    
    <h3>Scriptlet Example</h3>
    <%
        // This is a scriptlet
        String message = "Hello from JSP";
        out.println("<p>" + message + "</p>");
    %>
    
    <h3>JSP Expression</h3>
    <p>The time is: <%= new java.util.Date() %></p>
</body>
</html>