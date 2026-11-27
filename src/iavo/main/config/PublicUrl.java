package iavo.main.config;

import java.net.URI;

/** Adresse joignable par le téléphone qui scanne le QR code. */
public final class PublicUrl {
    private static final String DEFAULT_BASE = "http://172.16.1.156:8081/Medical";

    private PublicUrl() {
    }

    public static String base() {
        String configured = System.getProperty("medical.public.base.url");
        if (configured == null || configured.isBlank()) {
            configured = System.getenv("MEDICAL_PUBLIC_BASE_URL");
        }
        if (configured == null || configured.isBlank()) {
            configured = DEFAULT_BASE;
        }
        String base = configured.trim().replaceAll("/+$", "");
        URI uri = URI.create(base);
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null || uri.getQuery() != null || uri.getFragment() != null
                || uri.getUserInfo() != null) {
            throw new IllegalArgumentException("Adresse publique invalide : configurez MEDICAL_PUBLIC_BASE_URL.");
        }
        return base;
    }

    public static String patient(String id) {
        return base() + "/api/patient?id=" + id;
    }
}
