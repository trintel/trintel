package sopro.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sopro.TrintelApplication;
import sopro.service.backup.ExportInterface;
import sopro.service.backup.ImportInterface;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class BackupController {

    @Autowired
    ImportInterface importService;

    @Autowired
    ExportInterface exportService;

    /**
     * Manages the import of backup files and imports those. 
     *
     * @param path
     * @param model
     * @return String
     */
    @PostMapping("/backup/import")
    public String importBackup(@RequestParam String path, Model model) {
        TrintelApplication.logger.info("Importing file: " + path);
        importService.importJSON(path);

        return "redirect:/home"; // Admin muss irgendwie datei hochladen können, dann post request mit Pfad zur
                                 // datei and das hier.
    }

    /**
     * Exports all current data as json and lets the user download it.
     *
     * @param model
     * @return String
     */
    @GetMapping("/backup/export")
    public ResponseEntity<byte[]> exportAction(HttpServletResponse response, Model model) {

        String filePath = exportService.export();

        try {
            byte[] contents = Files.readAllBytes(new File(filePath).toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String[] soup = filePath.split("/"); // Last part is the filename
            String filename = soup[soup.length - 1];
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> res = new ResponseEntity<>(contents, headers, HttpStatus.OK);
            TrintelApplication.logger.info("Exporting to " + exportService.export());
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
