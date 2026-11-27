package iavo.main.dao;

import iavo.main.config.Database;
import iavo.main.model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PatientDao {
    private static final String COLUMNS = "id, nom, prenom, naissance, genre, adresse, telephone";

    public List<Patient> list() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM patient ORDER BY nom, prenom, id";
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                patients.add(map(result));
            }
        }
        return patients;
    }

    public Patient get(String id) throws SQLException {
        if (id == null) {
            return null;
        }
        String sql = "SELECT " + COLUMNS + " FROM patient WHERE id = ?";
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? map(result) : null;
            }
        }
    }

    public void create(Patient patient, String login, String passwordHash) throws SQLException {
        patient.id = UUID.randomUUID().toString();
        try (Connection connection = Database.open()) {
            connection.setAutoCommit(false);
            try {
                insert(connection, patient);
                UserDao.insert(connection, login, "PATIENT", patient.id, passwordHash);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                if ("23000".equals(e.getSQLState())) {
                    throw new IllegalArgumentException("Identifiant deja utilise.", e);
                }
                throw e;
            }
        }
    }

    public void insert(Connection connection, Patient patient) throws SQLException {
        String sql = "INSERT INTO patient (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patient.id);
            statement.setString(2, patient.nom);
            statement.setString(3, patient.prenom);
            statement.setString(4, patient.naissance);
            statement.setString(5, patient.genre);
            statement.setString(6, patient.adresse);
            statement.setString(7, patient.telephone);
            statement.executeUpdate();
        }
    }

    public void update(Patient patient) throws SQLException {
        String sql = "UPDATE patient SET nom=?, prenom=?, naissance=?, genre=?, adresse=?, telephone=? WHERE id=?";
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patient.nom);
            statement.setString(2, patient.prenom);
            statement.setString(3, patient.naissance);
            statement.setString(4, patient.genre);
            statement.setString(5, patient.adresse);
            statement.setString(6, patient.telephone);
            statement.setString(7, patient.id);
            if (statement.executeUpdate() == 0 && get(patient.id) == null) {
                throw new IllegalArgumentException("Patient introuvable.");
            }
        }
    }

    public boolean delete(String id) throws SQLException {
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM patient WHERE id = ?")) {
            statement.setString(1, id);
            return statement.executeUpdate() != 0;
        }
    }

    private Patient map(ResultSet result) throws SQLException {
        Patient patient = new Patient();
        patient.id = result.getString("id");
        patient.nom = result.getString("nom");
        patient.prenom = result.getString("prenom");
        patient.naissance = result.getString("naissance");
        patient.genre = result.getString("genre");
        patient.adresse = result.getString("adresse");
        patient.telephone = result.getString("telephone");
        return patient;
    }
}
