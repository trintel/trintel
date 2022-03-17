package sopro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sopro.TrintelApplication;
import sopro.service.backup.ExportInterface;
import sopro.service.backup.ImportInterface;

@Controller
public class BackupController {

    @Autowired
    ImportInterface importService;

    @Autowired
    ExportInterface exportService;

    /**
     * @param path
     * @param model
     * @return String
     */
    @PostMapping("/backup/import")
    public String importBackup(@RequestParam String path, Model model) {
        TrintelApplication.logger.info("Importing file: " + path);
        importService.importJSON(path);

        return "redirect:/home";  // Admin muss irgendwie datei hochladen können, dann post request mit Pfad zur datei and das hier.
    }



    /**
     * @param model
     * @return String
     */
    @GetMapping("/backup/export")
    public String exportBackup(Model model) {
        TrintelApplication.logger.info("Exporting to " + exportService.export());
        return "redirect:/home"; // Was soll hier returnt werden? Als was will Frontend die datei? Wie kann Admin sie hochladen?
    }
}
