<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%!
    // Déclaration de méthodes et variables au niveau de la page
    private int counter = 0;
    
    public String formatCurrency(double amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance();
        return formatter.format(amount);
    }
    
    public String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }
%>

<html>
<head>
    <title>Scriptlet Test Page</title>
    <style>
        .odd { background-color: #f2f2f2; }
        .even { background-color: #ffffff; }
        .error { color: red; }
    </style>
</head>
<body>
    <h1>Scriptlet Test Page</h1>
    
    <%-- Utilisation de scriptlets pour initialiser des données --%>
    <%
        // Initialisation de données pour le test
        java.util.List<String> names = new java.util.ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");
        names.add("Diana");
        
        java.util.Map<String, Double> products = new java.util.HashMap<>();
        products.put("Laptop", 999.99);
        products.put("Smartphone", 599.99);
        products.put("Tablet", 299.99);
        
        request.setAttribute("namesList", names);
        request.setAttribute("productsMap", products);
        
        // Simulation d'un calcul complexe
        double total = 0.0;
        for (Double price : products.values()) {
            total += price;
        }
        pageContext.setAttribute("totalPrice", total);
    %>
    
    <h2>Liste des noms (avec scriptlets)</h2>
    <ul>
    <% 
        for (int i = 0; i < names.size(); i++) {
            String rowClass = (i % 2 == 0) ? "even" : "odd";
    %>
        <li class="<%= rowClass %>">
            Nom <%= i+1 %>: <%= capitalize(names.get(i)) %> (longueur: <%= names.get(i).length() %>)
        </li>
    <% } %>
    </ul>
    
    <h2>Liste des noms (avec JSTL)</h2>
    <ul>
        <c:forEach var="name" items="${namesList}" varStatus="status">
            <li class="${status.index % 2 == 0 ? 'even' : 'odd'}">
                Nom ${status.count}: ${fn:toUpperCase(name)} (longueur: ${fn:length(name)})
            </li>
        </c:forEach>
    </ul>
    
    <h2>Produits et prix (avec scriptlets imbriqués)</h2>
    <table border="1">
        <tr>
            <th>Produit</th>
            <th>Prix</th>
        </tr>
        <% counter = 0; %>
        <% for (java.util.Map.Entry<String, Double> entry : products.entrySet()) { %>
            <tr>
                <td><%= entry.getKey() %></td>
                <td><%= formatCurrency(entry.getValue()) %></td>
            </tr>
            <% counter++; %>
        <% } %>
    </table>
    <p>Nombre de produits: <%= counter %></p>
    <p>Total: <%= formatCurrency(total) %></p>
    
    <%-- Scriptlet avec logique conditionnelle --%>
    <%
        boolean isAdmin = Math.random() > 0.5;
        if (isAdmin) {
    %>
        <div class="admin-panel">
            <h3>Panneau d'administration</h3>
            <p>Vous avez des privilèges d'administrateur.</p>
            <% for (int i = 0; i < 3; i++) { %>
                <button>Action <%= i+1 %></button>
            <% } %>
        </div>
    <%
        } else {
    %>
        <p class="error">Vous n'avez pas accès au panneau d'administration.</p>
    <%
        }
    %>
    
    <jsp:include page="include-fragment.jsp" />
    
    <footer>
        <p>Généré le <%= new java.util.Date() %></p>
    </footer>
</body>
</html>