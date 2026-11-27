package iavo.main.service;

import iavo.main.dao.PatientDao;
import iavo.main.model.Patient;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class PatientService {
    private final PatientDao patients = new PatientDao();

    public List<Patient> list() throws Exception {
        return patients.list();
    }

    public Patient get(String id) throws Exception {
        return patients.get(id);
    }

    public void save(Patient patient, String login, String password) throws Exception {
        validate(patient);
        if (patient.id == null || patient.id.isBlank()) {
            if (login == null || login.isBlank() || login.length() > 100 || password == null || password.length() < 10 || password.length() > 200) {
                throw new IllegalArgumentException("Identifiant et mot de passe patient (10 a 200 caracteres) requis.");
            }
            patients.create(patient, login, password);
        } else {
            patients.update(patient);
        }
    }

    public boolean delete(String id) throws Exception {
        return id != null && patients.delete(id);
    }

    private void validate(Patient patient) {
        if (patient.nom.isEmpty() || patient.prenom.isEmpty() || patient.naissance.isEmpty()
                || !Arrays.asList("Femme", "Homme", "Autre", "Non renseigne").contains(patient.genre)) {
            throw new IllegalArgumentException("Champs obligatoires invalides.");
        }
        if (patient.nom.length() > 100 || patient.prenom.length() > 100
                || patient.adresse.length() > 300 || patient.telephone.length() > 30) {
            throw new IllegalArgumentException("Champ trop long.");
        }
        if (LocalDate.parse(patient.naissance).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date invalide.");
        }
    }
}

