<%@ include file="../commun/header.jspf" %>
<%
    Patient patient = (Patient) request.getAttribute("patient");
    boolean edit = patient != null && patient.id != null && !patient.id.isEmpty();
    if (patient == null) {
        patient = new Patient();
    }
%>
<div class="heading">
    <div>
        <p class="eyebrow">DOSSIER PATIENT</p>
        <h1><%= edit ? "Modifier le patient" : "Nouveau patient" %></h1>
    </div>
    <a href="<%= base %>/tableau">← Retour</a>
</div>
<div class="card">
    <% if (request.getAttribute("erreur") != null) { %>
        <p class="error"><%= h(request.getAttribute("erreur")) %></p>
    <% } %>
    <form method="post" action="<%= base %>/patient/enregistrer">
        <input type="hidden" name="csrf" value="<%= h(token) %>">
        <input type="hidden" name="id" value="<%= h(patient.id) %>">
        <% if (edit) { %>
            <p>Identifiant unique : <strong><%= h(patient.id) %></strong></p>
            <div class="patient-qr">
                <img src="<%= base %>/patient/qrcode?id=<%= h(patient.id) %>" alt="QR code du patient">
                <p>Scannez ce QR code pour consulter les informations du patient au format JSON après connexion.</p>
                <a href="<%= base %>/patient/qrcode?id=<%= h(patient.id) %>" target="_blank" rel="noopener">Ouvrir le QR code</a>
            </div>
        <% } %>
        <div class="grid">
            <label>Nom *
                <input name="nom" value="<%= h(patient.nom) %>" maxlength="100" required>
            </label>
            <label>Prénom *
                <input name="prenom" value="<%= h(patient.prenom) %>" maxlength="100" required>
            </label>
            <label>Date de naissance *
                <input type="date" name="naissance" value="<%= h(patient.naissance) %>" required>
            </label>
            <label>Genre *
                <select name="genre" required>
                    <option value="">Choisir</option>
                    <% for (String genre : new String[]{"Femme", "Homme", "Autre", "Non renseigne"}) { %>
                        <option value="<%= h(genre) %>" <%= genre.equals(patient.genre) ? "selected" : "" %>>
                            <%= h(genre) %>
                        </option>
                    <% } %>
                </select>
            </label>
            <label>Adresse
                <input name="adresse" value="<%= h(patient.adresse) %>" maxlength="300">
            </label>
            <label>Téléphone
                <input name="telephone" value="<%= h(patient.telephone) %>" maxlength="30">
            </label>
        </div>
        <% if (!edit) { %>
            <h2>Compte patient</h2>
            <div class="grid">
                <label>Identifiant de connexion *
                    <input name="login" required autocomplete="off">
                </label>
                <label>Mot de passe *
                    <input type="password" name="password" minlength="10" required autocomplete="new-password">
                </label>
            </div>
        <% } %>
        <button>Enregistrer</button>
    </form>
</div>
<%@ include file="../commun/footer.jspf" %>




