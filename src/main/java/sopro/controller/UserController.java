package sopro.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.User;
import sopro.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/signup/student")
    public String signUpStudent(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "sign-up-student";
    }

    @GetMapping("/signup/admin")
    public String signUpAdmin(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "sign-up-admin";
    }

    @PostMapping("/signup/{role}")
    public String signedUpAdmin(@Valid User user, @PathVariable String role, BindingResult bindingResult, Model model) {
        user.setRole(role.toUpperCase());
        //TODO check if email exists
        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "sign-up";
        }
        String encPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);
    
        userRepository.save(user);
        return "redirect:login";
    }

    // @PostMapping("/signup/student")
    // public String signedUpUser(@Valid User user, BindingResult bindingResult, Model model) {
    //     user.setRole("STUDENT");
    //     //TODO check if email exists
    //     if(bindingResult.hasErrors()) {
    //         model.addAttribute("user", user);
    //         return "sign-up";
    //     }
    //     String encPassword = passwordEncoder.encode(user.getPassword());
    //     user.setPassword(encPassword);
    
    //     userRepository.save(user);
    //     return "redirect:login";
    // }
    
}
