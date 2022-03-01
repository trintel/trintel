package sopro.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.User;
import sopro.security.CustomUserDetailsService;
import sopro.security.WebSecurityConfig;

@Controller
public class HomeController {

    @Autowired
    WebSecurityConfig userService;

    @GetMapping("/")
    public String showHome() {
        System.out.println("Hello!");

        return "home";

    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "sign-up";
    }

    @PostMapping("/signup")
    public String signedUp(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        System.out.println("SignedUp");
        userService.signUpUser(user);
        return "redirect:login";
    }

    public void signUpUser(User user) {
        String encPassword = passwordEncoder().encode(user.getPassword());
        user.setPassword(encPassword);

        userRepository.save(user);
    }
}