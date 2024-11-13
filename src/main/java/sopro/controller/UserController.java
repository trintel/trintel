package sopro.controller;

import java.io.UnsupportedEncodingException;

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
import sopro.model.User;
import sopro.model.util.TokenStatus;
import sopro.repository.UserRepository;
import sopro.service.SignupUrlInterface;
import sopro.service.UserInterface;

@Controller
public class UserController {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserInterface userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    SignupUrlInterface signupUrlService;

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
            model.addAttribute("admin", true);
            return "user/sign-up";
        } else if (rndStr.equals(signupUrlService.getStudentSignupUrl())) {
            model.addAttribute("user", user);
            model.addAttribute("admin", false);

            return "user/sign-up";
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
            userService.createUser(user, role, request);
        } catch (IllegalArgumentException e) { // TODO: throw a more specific exception
            TrintelApplication.logger.error("Email Adresse schon registriert.\n\n" + e);

            model.addAttribute("isSignupError", true);
            model.addAttribute("signupErrorMsg",
                    messages.getMessage("mailAlreadyExists", null, "Mail already exitst.", request.getLocale()));
            model.addAttribute("admin", user.getRole().equals("admin"));
            return "user/sign-up";
        }

        return "user/verify-email";
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
        final TokenStatus tokenStatus = userService.validateVerificationToken(token, request);
        if (tokenStatus == TokenStatus.VALID)
            return new ModelAndView("redirect:/login", model); // Success you can now login.

        model.addAttribute("invalidLogin", "Registration token expired. Check your email for a new one.");
        return new ModelAndView("redirect:/login?error", model); // Bad user, expired.
    }

    @GetMapping("/user/settings")
    public String viewUserDetails(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userRepository.findById(user.getId()));
        return "user/settings";
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
        return "user/reset-password";
    }

    @PostMapping("/reset-password")
    public ModelAndView resetPassword(String email, RedirectAttributes ra, HttpServletRequest request) {
        userService.requestPasswordReset(email, request);
        ModelAndView mav = new ModelAndView("redirect:/reset-password?success");
        ra.addFlashAttribute("email", email);
        return mav;
    }

    @GetMapping("/reset-password/new")
    public String getSetNewPasswordForm() {
        return "user/set-new-password";
    }

    @PostMapping("/reset-password/new/{token}")
    public ModelAndView setNewPassword(@PathVariable String token, @RequestParam("password") String password) {
        final TokenStatus tokenStatus = userService.validateResetToken(token, password);
        if (tokenStatus == TokenStatus.VALID)
            return new ModelAndView("redirect:/login?resetPassword"); // Success you can now login.
        return new ModelAndView("redirect:/reset-password?invalidToken"); // Bad user, agelaufen.
    }
}
