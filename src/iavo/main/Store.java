package iavo.main;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Stockage local. Le nom des classes est conservé pour relire les données existantes. */
public class Store {
    public static class Patient implements Serializable {
        private static final long serialVersionUID = 1;
        public String id;
        public String nom;
        public String prenom;
        public String naissance;
        public String genre;
        public String adresse;
        public String telephone;
    }

    public static class User implements Serializable {
        private static final long serialVersionUID = 1;
        public String login;
        public String role;
        public String patientId;
        public String password;
    }

    private static class Data implements Serializable {
        private static final long serialVersionUID = 1;
        Map<String, Patient> patients = new LinkedHashMap<>();
        Map<String, User> users = new LinkedHashMap<>();
    }

    private static final Path FILE = Paths.get(
            System.getProperty("medical.data", System.getProperty("user.home") + File.separator + "medical-data"),
            "medical.bin");

    private static Data read() throws Exception {
        if (!Files.exists(FILE)) {
            return new Data();
        }
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(FILE))) {
            return (Data) input.readObject();
        }
    }

    private static void write(Data data) throws Exception {
        Files.createDirectories(FILE.getParent());
        Path temporaryFile = Files.createTempFile(FILE.getParent(), "medical-", ".tmp");
        try {
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(temporaryFile))) {
                output.writeObject(data);
            }
            try {
                Files.move(temporaryFile, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private static byte[] derive(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 210000, 256);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    }

    private static String hash(String password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(derive(password, salt));
    }

    public static synchronized User login(String name, String password) throws Exception {
        User user = read().users.get(name);
        if (user == null || password == null) {
            return null;
        }
        String[] parts = user.password.split(":");
        byte[] actual = derive(password, Base64.getDecoder().decode(parts[0]));
        byte[] expected = Base64.getDecoder().decode(parts[1]);
        return MessageDigest.isEqual(actual, expected) ? user : null;
    }

    public static synchronized void doctor(String name, String password) throws Exception {
        if (name == null || name.isBlank() || password == null || password.length() < 10) {
            throw new IllegalArgumentException("Identifiant requis et mot de passe de 10 caracteres minimum.");
        }
        Data data = read();
        if (data.users.containsKey(name)) {
            throw new IllegalArgumentException("Identifiant deja utilise.");
        }
        User user = new User();
        user.login = name;
        user.role = "MEDECIN";
        user.password = hash(password);
        data.users.put(name, user);
        write(data);
    }

    /** Lecture des anciens comptes pour la migration vers MySQL. */
    public static synchronized List<User> legacyUsers() throws Exception {
        return new ArrayList<>(read().users.values());
    }
    public static synchronized List<Patient> list() throws Exception {
        return new ArrayList<>(read().patients.values());
    }

    public static synchronized Patient get(String id) throws Exception {
        return read().patients.get(id);
    }

    public static synchronized void save(Patient patient, String name, String password) throws Exception {
        Data data = read();
        if (patient.id == null || patient.id.isBlank()) {
            if (name == null || name.isBlank() || password == null || password.length() < 10) {
                throw new IllegalArgumentException("Identifiant et mot de passe patient (10 caracteres minimum) requis.");
            }
            if (data.users.containsKey(name)) {
                throw new IllegalArgumentException("Identifiant deja utilise.");
            }
            patient.id = UUID.randomUUID().toString();
            User user = new User();
            user.login = name;
            user.password = hash(password);
            user.role = "PATIENT";
            user.patientId = patient.id;
            data.users.put(name, user);
        } else if (!data.patients.containsKey(patient.id)) {
            throw new IllegalArgumentException("Patient introuvable.");
        }
        data.patients.put(patient.id, patient);
        write(data);
    }

    public static synchronized void delete(String id) throws Exception {
        Data data = read();
        if (data.patients.remove(id) == null) {
            throw new IllegalArgumentException("Patient introuvable.");
        }
        data.users.values().removeIf(user -> id.equals(user.patientId));
        write(data);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1 || System.console() == null) {
            System.err.println("Usage interactif : java ... iavo.main.Store identifiant-medecin");
            System.exit(1);
        }
        char[] password = System.console().readPassword("Mot de passe du medecin : ");
        try {
            doctor(args[0], new String(password));
            System.out.println("Compte medecin cree.");
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}

