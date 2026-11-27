package iavo.main.service;

import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Compare les nouveaux mots de passe en clair et reconnaît les anciens comptes. */
public final class PasswordService {
    private PasswordService() {
    }

    public static boolean matches(String password, String stored) throws Exception {
        if (password == null || stored == null) {
            return false;
        }
        if (password.equals(stored)) {
            return true;
        }

        // Les anciennes données utilisaient salt:hash en PBKDF2.
        String[] parts = stored.split(":", -1);
        if (parts.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            if (salt.length != 16 || expected.length != 32) {
                return false;
            }
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 210000, 256);
            try {
                byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                        .generateSecret(spec).getEncoded();
                return MessageDigest.isEqual(actual, expected);
            } finally {
                spec.clearPassword();
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
