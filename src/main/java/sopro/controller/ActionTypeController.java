package sopro.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.ActionType;
import sopro.model.util.InitiatorType;
import sopro.repository.ActionTypeRepository;
import sopro.service.SignupUrlService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class ActionTypeController {

    @Autowired
    ActionTypeRepository actionTypeRepository;

    @GetMapping("/admin-panel")
    public String showActions(Model model) {
        model.addAttribute("signupUrlStudent", SignupUrlService.STUDENT_SIGNUP_URL);
        model.addAttribute("signupUrlAdmin", SignupUrlService.ADMIN_SIGNUP_URL);
        model.addAttribute("actionTypes", actionTypeRepository.findAll());
        return "admin/admin-panel";
    }

    @GetMapping("/action/add")
    public String addAction(Model model) {
        ActionType actionType = new ActionType();
        model.addAttribute("actionType", actionType);
        model.addAttribute("initiatorTypes", InitiatorType.values());
        return "admin/action-add";

    }

    @PostMapping("/action/save")
    public String saveAction(@Valid ActionType actionType, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("actionType", actionType);
            model.addAttribute("initiatorTypes", InitiatorType.values());
            return "admin/action-add";
        }

        actionTypeRepository.save(actionType);

        return "redirect:/admin-panel";
    }

    @GetMapping("action/edit/{actionTypeID}")
    public String editActionType(Model model, @PathVariable Long actionTypeID) {
        model.addAttribute("actionType", actionTypeRepository.findById(actionTypeID).get());
        model.addAttribute("initiatorTypes", InitiatorType.values());
        return "admin/action-edit";
    }

    @PostMapping("action/edit/{actionTypeID}")
    public String editAction(@Valid ActionType actionType, @PathVariable Long actionTypeID, BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("actionType", actionType);
            return "admin/action-edit";
        }
        actionType.setId(actionTypeID);
        actionTypeRepository.save(actionType);
        return "redirect:/admin-panel";

    }

}
