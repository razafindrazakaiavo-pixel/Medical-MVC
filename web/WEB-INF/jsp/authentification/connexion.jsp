<%@ include file="../commun/header.jspf" %>
<div class="card narrow">
    <p class="eyebrow">BIENVENUE</p>
    <h1>Connexion</h1>
    <p>Accédez à votre espace patient ou médecin.</p>
    <% if (request.getAttribute("erreur") != null) { %>
        <p class="error" role="alert"><%= h(request.getAttribute("erreur")) %></p>
    <% } %>
    <form method="post" action="<%= base %>/connexion">
        <input type="hidden" name="csrf" value="<%= h(token) %>">
        <label>
            Identifiant
            <input name="login" required autocomplete="username">
        </label>
        <label>
            Mot de passe
            <input type="password" name="password" required autocomplete="current-password">
        </label>
        <button>Se connecter</button>
    </form>
</div>
<%@ include file="../commun/footer.jspf" %>

