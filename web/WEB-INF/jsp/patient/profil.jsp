<%@ include file="../commun/header.jspf" %>
<%
    Patient patient = (Patient) request.getAttribute("patient");
%>
<p class="eyebrow">ESPACE PATIENT</p>
<h1>Mon profil</h1>
<div class="card">
    <% if (patient == null) { %>
        <p>Dossier introuvable.</p>
    <% } else { %>
        <dl>
            <dt>Identifiant unique</dt>
            <dd><%= h(patient.id) %></dd>
            <dt>Nom</dt>
            <dd><%= h(patient.nom) %> <%= h(patient.prenom) %></dd>
            <dt>Date de naissance</dt>
            <dd><%= h(patient.naissance) %></dd>
            <dt>Genre</dt>
            <dd><%= h(patient.genre) %></dd>
            <dt>Adresse</dt>
            <dd><%= h(patient.adresse) %></dd>
            <dt>Téléphone</dt>
            <dd><%= h(patient.telephone) %></dd>
        </dl>
    <% } %>
</div>
<%@ include file="../commun/footer.jspf" %>




