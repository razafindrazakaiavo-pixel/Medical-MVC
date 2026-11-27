package iavo.main.controller;

import iavo.main.model.Patient;
import iavo.main.model.User;
import iavo.main.service.PatientService;
import iavo.main.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.DateTimeException;


import java.util.UUID;
import src.ModelAndView;
import src.annotation.Controller;
import src.annotation.GetMapping;
import src.annotation.PostMapping;

@Controller
public class MedicalController {

    private final PatientService patients = new PatientService();
    private final UserService users = new UserService();

    private ModelAndView view(String name, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("csrf") == null) {
            session.setAttribute("csrf", UUID.randomUUID().toString());
        }
        return new ModelAndView(name);
    }

    private User user(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private boolean doctor(HttpServletRequest request) {
        User currentUser = user(request);
        return currentUser != null && "MEDECIN".equals(currentUser.role);
    }

    private boolean validCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String token = request.getParameter("csrf");
        return session != null && token != null && token.equals(session.getAttribute("csrf"));
    }

    private ModelAndView invalidSession(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return view("authentification/connexion", request)
                .addAttribute("erreur", "Votre session a expiré ou le formulaire n'est plus valide. Veuillez vous reconnecter.");
    }

    private ModelAndView error(HttpServletRequest request, HttpServletResponse response,
                               int status, String message) {
        response.setStatus(status);
        return view("commun/message", request).addAttribute("message", message);
    }

    @GetMapping("/accueil")
    public ModelAndView accueil(HttpServletRequest request) {
        return view("commun/accueil", request);
    }

    @GetMapping("/connexion")
    public ModelAndView connexion(HttpServletRequest request) {
        return view("authentification/connexion", request);
    }

    @PostMapping("/connexion")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!validCsrf(request)) {
            return invalidSession(request, response);
        }
        User currentUser = users.login(request.getParameter("login"), request.getParameter("password"));
        if (currentUser == null) {
            return error(request, response, 401, "Identifiant ou mot de passe incorrect.");
        }

        request.getSession().invalidate();
        HttpSession session = request.getSession(true);
        session.setAttribute("user", currentUser);
        session.setAttribute("csrf", UUID.randomUUID().toString());
        return tableau(request);
    }

    @PostMapping("/deconnexion")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        if (!validCsrf(request)) {
            return invalidSession(request, response);
        }
        request.getSession().invalidate();
        return view("commun/accueil", request);
    }

    @GetMapping("/tableau")
    public ModelAndView tableau(HttpServletRequest request) throws Exception {
        User currentUser = user(request);
        if (currentUser == null) {
            return view("authentification/connexion", request);
        }
        if (doctor(request)) {
            return view("patient/patients", request).addAttribute("patients", patients.list());
        }
        return view("patient/profil", request).addAttribute("patient", patients.get(currentUser.patientId));
    }

    @GetMapping("/patient/nouveau")
    public ModelAndView nouveau(HttpServletRequest request, HttpServletResponse response) {
        if (!doctor(request)) {
            return error(request, response, 403, "Acces reserve au medecin.");
        }
        return view("patient/formulaire", request);
    }

    @GetMapping("/patient/modifier")
    public ModelAndView modifier(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!doctor(request)) {
            return error(request, response, 403, "Acces reserve au medecin.");
        }
        Patient patient = patients.get(request.getParameter("id"));
        if (patient == null) {
            return error(request, response, 404, "Patient introuvable.");
        }
        return view("patient/formulaire", request).addAttribute("patient", patient);
    }

    @PostMapping("/patient/enregistrer")
    public ModelAndView enregistrer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!doctor(request)) {
            return error(request, response, 403, "Acces reserve au medecin.");
        }
        if (!validCsrf(request)) {
            return invalidSession(request, response);
        }

        Patient patient = new Patient();
        patient.id = request.getParameter("id");
        patient.nom = value(request, "nom");
        patient.prenom = value(request, "prenom");
        patient.naissance = value(request, "naissance");
        patient.genre = value(request, "genre");
        patient.adresse = value(request, "adresse");
        patient.telephone = value(request, "telephone");

        try {
            patients.save(patient, value(request, "login"), request.getParameter("password"));
            return tableau(request);
        } catch (IllegalArgumentException | DateTimeException e) {
            response.setStatus(400);
            return view("patient/formulaire", request)
                    .addAttribute("patient", patient)
                    .addAttribute("erreur", e.getMessage());
        }
    }

    @PostMapping("/patient/supprimer")
    public ModelAndView supprimer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!doctor(request)) {
            return error(request, response, 403, "Acces reserve au medecin.");
        }
        if (!validCsrf(request)) {
            return invalidSession(request, response);
        }
        String id = request.getParameter("id");
        if (id == null || patients.get(id) == null) {
            return error(request, response, 404, "Patient introuvable.");
        }
        patients.delete(id);
        return tableau(request);
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}


