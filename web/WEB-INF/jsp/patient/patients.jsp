<%@ include file="../commun/header.jspf" %>
<%@ page import="java.util.List" %>
<%
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
%>
<div class="heading">
    <div>
        <p class="eyebrow">ESPACE MÉDECIN</p>
        <h1>Patients</h1>
        <p>Gérez les dossiers et les comptes patients.</p>
    </div>
    <a class="button" href="<%= base %>/patient/nouveau">+ Nouveau patient</a>
</div>
<div class="card table-wrap">
    <% if (patients.isEmpty()) { %>
        <p>Aucun patient enregistré.</p>
    <% } else { %>
        <table>
            <thead>
                <tr>
                    <th>Patient</th>
                    <th>Identifiant unique</th>
                    <th>Naissance</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (Patient patient : patients) { %>
                    <tr>
                        <td>
                            <strong><%= h(patient.nom) %> <%= h(patient.prenom) %></strong><br>
                            <small><%= h(patient.telephone) %></small>
                        </td>
                        <td><small><%= h(patient.id) %></small></td>
                        <td><%= h(patient.naissance) %></td>
                        <td>
                            <a href="<%= base %>/patient/modifier?id=<%= h(patient.id) %>">Voir / modifier</a>
                            <form class="inline" method="post" action="<%= base %>/patient/supprimer"
                                  onsubmit="return confirm('Supprimer ce patient et son compte ?')">
                                <input type="hidden" name="csrf" value="<%= h(token) %>">
                                <input type="hidden" name="id" value="<%= h(patient.id) %>">
                                <button class="danger link">Supprimer</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>
</div>
<%@ include file="../commun/footer.jspf" %>


