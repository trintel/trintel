package sopro.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    
    /** 
     * GET routing für Index.
     * 
     * @return page Home
     */
    @GetMapping("/")
    public String showHome() {
        return "home";
    }

  
}