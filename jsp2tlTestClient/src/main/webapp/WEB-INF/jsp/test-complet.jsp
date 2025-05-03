<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Test Scriplets</title>
</head>
<body>
    <h1>Test de conversion des scriplets</h1>
    
    <%-- Test scriplet simple --%>
    <% int x = 5; %>
    
    <%-- Test scriplet avec expression --%>
    <p>La valeur est : <%= x %></p>
    
    <%-- Test scriplet avec logique --%>
    <% if(x > 3) { %>
        <p>X est plus grand que 3</p>
    <% } %>
    
    <%-- Test scriplet avec boucle --%>
    <ul>
    <% for(int i=0; i<3; i++) { %>
        <li>Item <%= i %></li>
    <% } %>
    </ul>
</body>
</html>
