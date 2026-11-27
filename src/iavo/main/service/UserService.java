package iavo.main.service;

import iavo.main.dao.UserDao;
import iavo.main.model.User;
import java.util.Arrays;

public class UserService {
    private final UserDao users = new UserDao();

    public User login(String login, String password) throws Exception {
        return users.login(login, password);
    }

    public void createDoctor(String login, String password) throws Exception {
        if (login == null || login.isBlank() || login.length() > 100 || password == null || password.length() < 10 || password.length() > 200) {
            throw new IllegalArgumentException("Identifiant requis et mot de passe entre 10 et 200 caracteres.");
        }
        users.createDoctor(login, password);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1 || System.console() == null) {
            System.err.println("Usage interactif : java ... iavo.main.service.UserService identifiant-medecin");
            System.exit(1);
        }
        char[] password = System.console().readPassword("Mot de passe du medecin : ");
        if (password == null) {
            throw new IllegalArgumentException("Mot de passe requis.");
        }
        try {
            new UserService().createDoctor(args[0], new String(password));
            System.out.println("Compte medecin cree.");
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}

