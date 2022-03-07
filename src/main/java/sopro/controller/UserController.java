package sopro.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import sopro.events.OnRegistrationCompleteEvent;
import sopro.model.User;
import sopro.repository.UserRepository;
import sopro.service.UserInterface;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    UserInterface userService;

    /**
     * GET routing für Index.
     * 
     * @return page Home
     */
    @GetMapping("/home")
    public String showHome2(@AuthenticationPrincipal User user) {

        if (user.getCompany() != null || user.getRole().equals("ADMIN")) { // just go to the home page if a company is
                                                                           // already selected or the user is admin
            return "home";
        }

        return "redirect:/company/select"; // have students select a company if non is selected
    }

    /**
     * Returns signup page for students.
     *
     * @param model
     * @return page
     */
    @GetMapping("/signup/student")
    public String signUpStudent(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "sign-up-student";
    }

    /**
     * Returns signup page for admins.
     * 
     * @param model
     * @return page
     */
    @GetMapping("/signup/admin")
    public String signUpAdmin(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "sign-up-admin";
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
    public String signedUp(@Valid User user, HttpServletRequest request, @PathVariable String role,
            BindingResult bindingResult, Model model) {

        // TODO check mail exists
        user.setRole(role.toUpperCase());
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "sign-up";
        }
        // encode the new password for saving in the database
        String encPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        // TODO move the registerNewUser part to UserService.

        // saves the new user in userRepo
        userRepository.save(user);

        // Publish event for Mail validation.
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), request.getServerName() + ":" + request.getServerPort()));
        return "verify-your-email";
    }

    /** 
     * Handles the email registration link.
     * 
     * @param request
     * @param model
     * @param token
     * @return ModelAndView
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token) throws UnsupportedEncodingException {
        final String result = userService.validateVerificationToken(token);
        if (result.equals("valid"))
            return new ModelAndView("redirect:/login", model); // Success you can now login. 
        
        model.addAttribute("invalidLogin", "Registration token expired.");
        return new ModelAndView("redirect:/login?error", model); // Bad user, agelaufen.
    }

}
