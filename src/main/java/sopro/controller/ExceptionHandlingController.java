package sopro.controller;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import sopro.TrintelApplication;

@ControllerAdvice
public class ExceptionHandlingController {

  // Exception handling methods


  // Specify name of a specific view that will be used to display the error:
  @ExceptionHandler({MaxUploadSizeExceededException.class, NestedServletException.class, SizeLimitExceededException.class})
  public String databaseError(Model model) {
    TrintelApplication.logger.error("File upload too large");
    model.addAttribute("error", "File exceeds maximum upload size");
    model.addAttribute("status", "500");
    model.addAttribute("timestamp", new Timestamp(System.currentTimeMillis()));

    return "error";
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView handleError(HttpServletRequest req, Exception ex) {
    TrintelApplication.logger.error("Request: " + req.getRequestURL() + " raised " + ex);

    ModelAndView mav = new ModelAndView();
    mav.addObject("exception", ex);
    mav.addObject("url", req.getRequestURL());
    mav.setViewName("error");
    return mav;
  }

}
