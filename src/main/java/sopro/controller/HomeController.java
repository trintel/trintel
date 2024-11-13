package sopro.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import sopro.model.User;
import sopro.repository.UserRepository;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

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
        return "home/login";
    }

    /**
     * GET routing für Index.
     *
     * @return page Home
     */
    @GetMapping("/home")
    public String showHome2(@AuthenticationPrincipal User user, Model model) {
        if (user.getCompany() != null || user.getRole().equals("ADMIN")) { // just go to the home page if a company is
                                                                           // already selected or the user is admin
            Optional<User> profile = userRepository.findById(user.getId());
            if (profile.isPresent()) {
                model.addAttribute("forename", profile.get().getForename());
            }
            return "home/index";
        }

        return "redirect:/companies"; // have students select a company if non is selected
    }

}
