package lk.ijse.gdse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/assets/*")
public class AssetsServlet extends HttpServlet {
    private final String assets = "C:\\Users\\User\\OneDrive\\Documents\\AAD\\JAVAEE\\WORK\\EMS-FN\\assets";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filename = req.getPathInfo();
        if (filename == null || filename.isEmpty() || filename.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }
        File file = new File(assets + filename);
        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }
       String type = getServletContext().getMimeType(file.getName());
        if (type == null) {
            type = "application/octet-stream";
        }
        resp.setContentType(type);
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}
