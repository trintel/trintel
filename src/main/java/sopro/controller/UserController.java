package sopro.controller;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sopro.TrintelApplication;
import sopro.events.OnRegistrationCompleteEvent;
import sopro.model.User;
import sopro.repository.UserRepository;
import sopro.service.SignupUrlInterface;
import sopro.service.UserInterface;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    UserInterface userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    SignupUrlInterface signupUrlService;

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
            if(profile.isPresent()) {
                model.addAttribute("forename", profile.get().getForename());
            }                                                               
            return "home";
        }

        return "redirect:/company/select"; // have students select a company if non is selected
    }

    /**
     * Returns signup page for admins.
     *
     * @param model
     * @return page
     */
    @GetMapping("/signup/{rndStr}")
    public String signupRedirect(@PathVariable String rndStr, Model model) {
        User user = new User();
        if (rndStr.equals(signupUrlService.getAdminSignupUrl())) {
            model.addAttribute("user", user);
            return "sign-up-admin";
        } else if (rndStr.equals(signupUrlService.getStudentSignupUrl())){
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
    public String signup(@Valid User user, HttpServletRequest request, @PathVariable String role,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            if (role.equals("admin")) {
                return "sign-up-admin";
            } else {
                return "sign-up-student";
            }
        }

        try {
            userService.createUser(user, role);
        } catch (Exception e) {
            TrintelApplication.logger.error("Email Adresse schon registriert.\n\n" + e);

            model.addAttribute("isSignupError", true);
            model.addAttribute("signupErrorMsg", messages.getMessage("mailAlreadyExists", null, "Mail already exitst.", request.getLocale()));
            if (role.equals("admin")) {
                return "sign-up-admin";
            } else {
                return "sign-up-student";
            }
        }

        // Publish event for Mail validation.
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(),
                request.getServerName() + ":" + request.getServerPort()));
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
    public ModelAndView confirmRegistration(final HttpServletRequest request, final ModelMap model,
            @RequestParam("token") final String token) throws UnsupportedEncodingException {
        final String result = userService.validateVerificationToken(token);
        if (result.equals("valid"))
            return new ModelAndView("redirect:/login", model); // Success you can now login.

        model.addAttribute("invalidLogin", "Registration token expired.");
        return new ModelAndView("redirect:/login?error", model); // Bad user, agelaufen.
    }

    @GetMapping("/user/settings")
    public String viewUserDetails(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userRepository.findById(user.getId()));
        return "settings";
    }

    @PostMapping("/user/change-password")
    public String changePassword(String password, @AuthenticationPrincipal User user) {
        userService.changePassword(user, password);
        return "redirect:/user/settings?password";
    }

    @PostMapping("/user/change-name")
    public String changeName(String forename, String surname, @AuthenticationPrincipal User user) {
        userService.changeName(user, forename, surname);
        return "redirect:/user/settings?name";
    }

    @GetMapping("/reset-password")
    public String getResetPasswordForm() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public ModelAndView resetPassword(String email, RedirectAttributes ra) {
        ModelAndView mav = new ModelAndView("redirect:/reset-password?success");
        ra.addFlashAttribute("email", email);
        return mav;
    }

    @GetMapping("/reset-password/new")
    public String getSetNewPasswordForm() {
        return "set-new-password";
    }

    @PostMapping("/reset-password/new")
    public String setNewPassword() {
        return "redirect:/login?resetPassword";
    }
}
