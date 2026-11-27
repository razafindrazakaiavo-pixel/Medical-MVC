package iavo.main.dao;

import iavo.main.config.Database;
import iavo.main.model.User;
import iavo.main.service.PasswordService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public User login(String login, String password) throws Exception {
        String sql = "SELECT login, role, patient_id, password_hash FROM utilisateur WHERE login = ?";
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !PasswordService.matches(password, result.getString("password_hash"))) {
                    return null;
                }
                User user = new User();
                user.login = result.getString("login");
                user.role = result.getString("role");
                user.patientId = result.getString("patient_id");
                return user;
            }
        }
    }

    public void createDoctor(String login, String passwordHash) throws SQLException {
        try (Connection connection = Database.open()) {
            insert(connection, login, "MEDECIN", null, passwordHash);
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                throw new IllegalArgumentException("Identifiant deja utilise.", e);
            }
            throw e;
        }
    }

    public static void insert(Connection connection, String login, String role, String patientId,
                              String passwordHash) throws SQLException {
        String sql = "INSERT INTO utilisateur (login, role, patient_id, password_hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, role);
            statement.setString(3, patientId);
            statement.setString(4, passwordHash);
            statement.executeUpdate();
        }
    }
}
