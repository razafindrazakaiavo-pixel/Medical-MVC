package iavo.main.controller;

import iavo.main.model.Patient;
import iavo.main.model.User;
import jakarta.servlet.http.HttpSession;
import iavo.main.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import src.annotation.Controller;
import src.annotation.GetMapping;
import src.annotation.UrlApi;

/** Informations minimales accessibles aux utilisateurs autorises. */
@Controller
public class PatientPublicController {
    private final PatientService patients = new PatientService();

    @GetMapping("/api/patient")
    @UrlApi
    public Map<String, Object> patient(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        String id = request.getParameter("id");
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            if (id == null || !UUID.fromString(id).toString().equals(id)) {
                throw new IllegalArgumentException("Identifiant invalide");
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("erreur", "Identifiant patient invalide.");
            return result;
        }

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("erreur", "Connectez-vous pour consulter ce patient.");
            return result;
        }
        if (!"MEDECIN".equals(user.role) && !("PATIENT".equals(user.role) && id.equals(user.patientId))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            result.put("erreur", "Accès non autorisé à ce patient.");
            return result;
        }
        Patient patient = patients.get(id);
        if (patient == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result.put("erreur", "Patient introuvable.");
            return result;
        }
        result.put("id", patient.id);
        result.put("nom", patient.nom);
        result.put("prenom", patient.prenom);
        return result;
    }
}

