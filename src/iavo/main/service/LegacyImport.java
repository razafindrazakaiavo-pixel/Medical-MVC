package iavo.main.service;

import iavo.main.Store;
import iavo.main.config.Database;
import iavo.main.dao.PatientDao;
import iavo.main.dao.UserDao;
import iavo.main.model.Patient;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

/** Importe les anciennes données sans modifier le fichier d'origine. */
public class LegacyImport {
    public static void main(String[] args) throws Exception {
        Path directory = Paths.get(System.getProperty("medical.data",
                System.getProperty("user.home") + File.separator + "medical-data"));
        if (!Files.isRegularFile(directory.resolve("medical.bin"))) {
            throw new IllegalStateException("Ancien fichier medical.bin introuvable dans " + directory);
        }
        PatientDao patients = new PatientDao();
        try (Connection connection = Database.open()) {
            connection.setAutoCommit(false);
            try {
                int patientCount = 0;
                int userCount = 0;
                for (Store.Patient old : Store.list()) {
                    Patient patient = new Patient();
                    patient.id = old.id;
                    patient.nom = old.nom;
                    patient.prenom = old.prenom;
                    patient.naissance = old.naissance;
                    patient.genre = old.genre;
                    patient.adresse = old.adresse;
                    patient.telephone = old.telephone;
                    patients.insert(connection, patient);
                    patientCount++;
                }
                for (Store.User user : Store.legacyUsers()) {
                    UserDao.insert(connection, user.login, user.role, user.patientId, user.password);
                    userCount++;
                }
                connection.commit();
                System.out.println("Import termine : " + patientCount + " patients, " + userCount + " comptes.");
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
    }
}
