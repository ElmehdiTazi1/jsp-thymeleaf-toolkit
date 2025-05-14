<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date, java.text.SimpleDateFormat, java.util.ArrayList" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JSP Scriptlet Example</title>
</head>
<body>
    <h1>JSP Scriptlet Examples</h1>
    
    <%-- Declaration Scriptlet --%>
    <%! 
        // Class-level variable declaration
        private int accessCount = 0;
        
        // Method declaration
        public String formatDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        }
        
        // Increment and return access count
        public synchronized int incrementAccessCount() {
            return ++accessCount;
        }
    %>
    
    <%-- Standard Scriptlet --%>
    <h2>Current Server Information:</h2>
    <% 
        // Java code block
        Date currentDate = new Date();
        ArrayList<String> serverInfo = new ArrayList<>();
        serverInfo.add("Server Name: " + request.getServerName());
        serverInfo.add("Server Port: " + request.getServerPort());
        serverInfo.add("Server Protocol: " + request.getProtocol());
        
        // Conditional logic
        boolean isSecure = request.isSecure();
        if (isSecure) {
            serverInfo.add("Connection: Secure (HTTPS)");
        } else {
            serverInfo.add("Connection: Standard (HTTP)");
        }
        
        // Loop through the list
        for (String info : serverInfo) {
    %>
        <p><%= info %></p>
    <% } %>
    
    <%-- Expression Scriptlet --%>
    <h2>Dynamic Content:</h2>
    <p>Current date and time: <%= formatDate(currentDate) %></p>
    <p>Page Access Count: <%= incrementAccessCount() %></p>
    <p>Your IP Address: <%= request.getRemoteAddr() %></p>
    <p>JSP File Location: <%= application.getRealPath(request.getServletPath()) %></p>
    
    <%-- Using scriptlet to set variables for later use --%>
    <% 
        String userAgent = request.getHeader("User-Agent");
        String browser = "Unknown";
        
        if (userAgent != null) {
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                browser = "Internet Explorer";
            } else if (userAgent.contains("Firefox")) {
                browser = "Mozilla Firefox";
            } else if (userAgent.contains("Chrome")) {
                browser = "Google Chrome";
            } else if (userAgent.contains("Safari")) {
                browser = "Safari";
            } else if (userAgent.contains("Opera")) {
                browser = "Opera";
            }
        }
    %>
    
    <h2>Browser Detection:</h2>
    <p>Your browser appears to be: <%= browser %></p>
    <p>Full User-Agent: <%= userAgent %></p>
    
    <h2>Query String Parameters:</h2>
    <% 
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
    %>
        <p>The following parameters were provided:</p>
        <ul>
            <% 
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
            %>
                <li><strong><%= keyValue[0] %>:</strong> <%= keyValue[1] %></li>
            <%
                    }
                }
            %>
        </ul>
    <% } else { %>
        <p>No query string parameters were provided.</p>
    <% } %>
</body>
</html>