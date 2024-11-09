package sopro.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import sopro.TrintelApplication;
import sopro.model.User;
import sopro.service.ExcelExportInterface;
import sopro.service.ExportImportInterface;
import sopro.service.InitDatabaseService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class BackupController {

    @Autowired
    ExportImportInterface exportImportService;

    @Autowired
    ExcelExportInterface excelExportService;

    @Autowired
    InitDatabaseService initDatabaseService;
    /**
     * Manages the import of backup files and imports those.
     *
     * @param path
     * @param model
     * @return String
     */
    @PostMapping("/backup/import")
    public String importBackup(@RequestParam("file") MultipartFile importFile, Model model) {
        TrintelApplication.logger.info("Importing file: " + importFile.getOriginalFilename());
        File file = new File(TrintelApplication.EXPORT_PATH + "/trintelImport.sql");

        if (!importFile.getOriginalFilename().contains(".sql")) {
            TrintelApplication.logger.error("Tried to import a non sql file: "+ importFile.getOriginalFilename());
            return "redirect:/home";
        }

        try {
            importFile.transferTo(file);
        } catch (IllegalStateException | IOException e) {
            TrintelApplication.logger.info("Error " + e.getMessage());
            e.printStackTrace();
        }
        exportImportService.importSQL(TrintelApplication.EXPORT_PATH + "/trintelImport.sql");

        TrintelApplication.logger.info("Quitting Application...");

        SpringApplication.exit(TrintelApplication.context, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                // no errors
                return 0;
            }
        });
        return "redirect:/home"; // Admin muss irgendwie datei hochladen können, dann post request mit Pfad zur
                                 // datei and das hier.
    }

    /**
     * Exports all current data as SQL dump and lets the user download it.
     *
     * @param model
     * @return String
     */
    @GetMapping("/backup/export")
    public ResponseEntity<byte[]> exportAction(HttpServletResponse response, Model model) {

        String filePath = exportImportService.export();

        try {
            byte[] contents = Files.readAllBytes(new File(filePath).toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String[] soup = filePath.split("/"); // Last part is the filename
            String filename = soup[soup.length - 1];
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> res = new ResponseEntity<>(contents, headers, HttpStatus.OK);
            TrintelApplication.logger.info("Exporting to " + exportImportService.export());
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/excel-report")
    public ResponseEntity<byte[]> excelReport(HttpServletResponse response, Model model) {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Trintel_report_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        excelExportService.excelReport(response);
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reset")
    public String resetDatabase(Model model, @AuthenticationPrincipal User user) {
        TrintelApplication.logger.info("Resetting Database to Default. Keeping User: " + user.getEmail());

        initDatabaseService.resetWithAdmin(user);

        TrintelApplication.logger.info("Quitting Application...");

        SpringApplication.exit(TrintelApplication.context, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                // no errors
                return 0;
            }
        });
        return "redirect:/login";
    }
}
