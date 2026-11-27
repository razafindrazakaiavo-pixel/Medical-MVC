package iavo.main.servlet;

import iavo.main.model.User;
import iavo.main.service.PatientService;
import iavo.main.service.QrCodeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Sert le QR code uniquement au médecin ou au patient concerné. */
public class QrCodeServlet extends HttpServlet {
    private final PatientService patients = new PatientService();
    private final QrCodeService qrCodes = new QrCodeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String id = request.getParameter("id");
        if (id == null || (!"MEDECIN".equals(user.role)
                && !("PATIENT".equals(user.role) && id.equals(user.patientId)))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if (patients.get(id) == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Path image = qrCodes.getOrCreate(id);
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "private, no-store");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setContentLengthLong(Files.size(image));
            Files.copy(image, response.getOutputStream());
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            throw new ServletException("Impossible de générer le QR code.", e);
        }
    }
}
