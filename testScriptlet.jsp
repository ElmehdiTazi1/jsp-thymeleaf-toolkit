<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Test Scriptlet</title>
</head>
<body>
    <h1>Test de Scriptlet</h1>
    <p>
        <% 
            String message = "Bonjour, ceci est une scriptlet JSP.";
            boolean test = true;
            if (test) {
                out.println(message);
            } else {
                out.println("La condition n'est pas remplie.");
            }
        %>
    </p>
</body>
</html>
