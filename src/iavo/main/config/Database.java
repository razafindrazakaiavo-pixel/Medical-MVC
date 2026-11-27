package iavo.main.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private Database() {
    }

    public static Connection open() throws SQLException {
        String url = setting("MEDICAL_DB_URL", "jdbc:mysql://localhost:3306/medical?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Africa/Nairobi");
        String user = setting("MEDICAL_DB_USER", "root");
        String password = setting("MEDICAL_DB_PASSWORD", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Pilote MySQL introuvable : ajoutez mysql-connector-j dans lib.", e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null ? defaultValue : value;
    }
}
