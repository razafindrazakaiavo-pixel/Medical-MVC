package iavo.main.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import iavo.main.config.PublicUrl;
import iavo.main.model.Patient;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;

/** Genere un QR code local contenant une URL publique du patient. */
public class QrCodeService {
    private static final int SIZE = 280;
    private final Path directory;

    public QrCodeService() {
        String defaultDirectory = System.getProperty("user.home") + File.separator
                + "medical-data" + File.separator + "qrcodes";
        directory = Paths.get(System.getProperty("medical.qr.dir", defaultDirectory));
    }

    public synchronized Path getOrCreate(String patientId) throws Exception {
        String canonicalId = UUID.fromString(patientId).toString();
        if (!canonicalId.equals(patientId)) {
            throw new IllegalArgumentException("Identifiant patient invalide.");
        }
        Files.createDirectories(directory);
        String url = PublicUrl.patient(canonicalId);
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(url.getBytes(StandardCharsets.UTF_8));
        StringBuilder version = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            version.append(String.format("%02x", digest[i] & 0xff));
        }
        Path destination = directory.resolve(canonicalId + "-" + version + ".png");
        if (Files.isRegularFile(destination)) {
            return destination;
        }

        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 4);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, SIZE, SIZE, hints);
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        Path temporaryFile = Files.createTempFile(directory, "qr-", ".tmp");
        try {
            if (!ImageIO.write(image, "PNG", temporaryFile.toFile())) {
                throw new IOException("Impossible de créer le fichier PNG.");
            }
            try {
                Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, destination);
            } catch (java.nio.file.FileAlreadyExistsException e) {
                // Un autre processus a généré le même fichier entre-temps.
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
        return destination;
    }

    /** Programme autonome : crée les QR codes absents pour tous les patients. */
    public static void main(String[] args) throws Exception {
        QrCodeService service = new QrCodeService();
        int count = 0;
        for (Patient patient : new PatientService().list()) {
            service.getOrCreate(patient.id);
            count++;
        }
        System.out.println("QR codes disponibles pour " + count + " patient(s) : " + service.directory.toAbsolutePath());
    }
}

