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
        return "redirect:/home";
    }

    /**
     * GET routing für Index.
     *
     * @return page Home
     */
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

}
