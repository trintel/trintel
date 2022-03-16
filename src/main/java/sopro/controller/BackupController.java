package sopro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sopro.service.backup.ImportInterface;

@Controller
public class BackupController {

    @Autowired
    ImportInterface importService;

    @PostMapping("/backup/import")
    public String moveToCompany(@RequestParam String path, Model model) {
        System.out.println("Import "+ path);
        importService.importJSON(path);

        return "redirect:/login";
    }
}
