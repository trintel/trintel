package sopro.controller;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import sopro.TrintelApplication;

@ControllerAdvice
public class ExceptionHandlingController {

  // Exception handling methods


  // Specify name of a specific view that will be used to display the error:
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public String databaseError(Model model) {
    TrintelApplication.logger.error("File upload too large");
    model.addAttribute("error", "File exceeds maximum upload size");
    model.addAttribute("status", "500");
    model.addAttribute("timestamp", new Timestamp(System.currentTimeMillis()));

    return "error";
  }

  @ExceptionHandler(Exception.class)
  public String handleError(HttpServletRequest req, Exception ex, Model model) {
    TrintelApplication.logger.error("Request: " + req.getRequestURL() + " raised " + ex);
    model.addAttribute("error", ex.getMessage());
    model.addAttribute("status", "500");
    model.addAttribute("timestamp", new Timestamp(System.currentTimeMillis()));
    return "error";
  }

}
