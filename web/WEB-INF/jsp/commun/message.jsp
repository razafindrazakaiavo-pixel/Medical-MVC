<%@ include file="header.jspf" %>
<div class="card">
    <h1>Information</h1>
    <p><%= h(request.getAttribute("message")) %></p>
    <a href="<%= base %>/accueil">Retour à l'accueil</a>
</div>
<%@ include file="footer.jspf" %>
