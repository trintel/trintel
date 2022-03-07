package sopro.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.TrintelApplication;
import sopro.model.User;
import sopro.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /** 
     * GET routing für Index.
     * 
     * @return page Home
     */
    @GetMapping("/home")
    public String showHome2(@AuthenticationPrincipal User user) {

        if(user.getCompany() != null || user.getRole().equals("ADMIN")) {   //just go to the home page if a company is already selected or the user is admin
            return "home";
        }

        return "redirect:/company/select";    //have students select a company if non is selected
    }

    /**
     * Returns signup page for admins.
     * 
     * @param model
     * @return page
     */
    @GetMapping("/signup/{rndStr}")
    public String signUpAdmin(@PathVariable String rndStr, Model model) {
        User user = new User();
        if (rndStr.equals(TrintelApplication.ADMIN_LOGIN_URL)) {
            model.addAttribute("user", user);
            return "sign-up-admin";
        } else if (rndStr.equals(TrintelApplication.STUDENT_LOGIN_URL)){
            model.addAttribute("user", user);
            return "sign-up-student";
        } else {
            return "redirect:/login";
        }

    }

    /**
     * Receives POST requests from the signup pages.
     * Extracts role form path.
     * Validates inputs and saves when all good.
     * 
     * @param user
     * @param role
     * @param bindingResult
     * @param model
     * @return redirect:login
     */
    @PostMapping("/signup/{role}")
    public String signedUp(@Valid User user, @PathVariable String role, BindingResult bindingResult, Model model) {
        user.setRole(role.toUpperCase());
        // TODO check if email exists
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            if(role.equals("admin")) {
                return "sign-up-admin";
            } else {
                return "sign-up-student";
            }
        }
        // encode the new password for saving in the database
        String encPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        // saves the new user in userRepo
        userRepository.save(user);

        return "redirect:/login";
    }
    
}
