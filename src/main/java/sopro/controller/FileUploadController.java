package sopro.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sopro.storage.StorageFileNotFoundException;
import sopro.storage.StorageService;


@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    // @GetMapping("/")
    // public String listUploadedFiles(Model model) throws IOException {

    // 	model.addAttribute("files", storageService.loadAll().map(
    // 			path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
    // 					"serveFile", path.getFileName().toString()).build().toUri().toString())
    // 			.collect(Collectors.toList()));

    // 	return "uploadForm";
    // }

    // @GetMapping("/files/{filename:.+}")
    // @ResponseBody
    // public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

    // 	Resource file = storageService.loadAsResource(filename);
    // 	return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
    // 			"attachment; filename=\"" + file.getFilename() + "\"").body(file);
    // }

    @PostMapping("/companies/{companyID}/edit/upload-logo")     //TODO: Maybe check if User is Authorised to do that (But Post, so....)
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @PathVariable Long companyID) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/company/" + companyID + "/edit";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }


}
