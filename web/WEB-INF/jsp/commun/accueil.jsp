<%@ include file="header.jspf" %>
<section class="hero">
    <p class="eyebrow">ESPACE MÉDICAL</p>
    <h1>Un espace simple pour suivre vos patients.</h1>
    <p>Les médecins gèrent les dossiers patients. Chaque patient peut consulter ses propres informations.</p>
    <a class="button" href="<%= base %>/<%= current == null ? "connexion" : "tableau" %>">
        <%= current == null ? "Se connecter" : "Accéder à mon espace" %>
    </a>
</section>
<%@ include file="footer.jspf" %>
