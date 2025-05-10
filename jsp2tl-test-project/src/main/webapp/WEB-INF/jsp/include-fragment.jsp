<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="fragment">
    <h4>Fragment inclus</h4>
    <p>Ce fragment est inclus dans la page principale via jsp:include.</p>
    
    <% if (request.getParameter("showDetails") != null) { %>
        <div class="details">
            <p>Détails supplémentaires:</p>
            <ul>
                <li>Session ID: <%= session.getId() %></li>
                <li>Heure actuelle: <%= new java.util.Date() %></li>
            </ul>
        </div>
    <% } %>
</div>