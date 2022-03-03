package sopro.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.Company;
import sopro.model.User;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;
@Controller
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;
    
// #######################################################################################
// ----------------------------------- ADMIN FUNCTIONS -----------------------------------
// #######################################################################################

    @GetMapping("/companies")
    public String listCompanies(Model model) {
        model.addAttribute("companies", companyRepository.findAll()); //add a list of all companies to the model
        return "company-list";
    }

    @GetMapping("/companies/add")
    public String addCompany(Model model) {
        Company company = new Company();    //creating a new Company Object to be added to the database
        model.addAttribute("company", company); 
        return "company-create";
    }

    @PostMapping("/companies/save")
    public String saveCompany(@Valid Company company, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
			model.addAttribute("company", company);
			return "company-create";
		}
        companyRepository.save(company);    //saves the new Company
        return "redirect:/companies";

    }


// #########################################################################################
// ----------------------------------- STUDENT FUNCTIONS -----------------------------------
// #########################################################################################

    @GetMapping("/company/select")
    public String selectCompany(Model model) {
        model.addAttribute("companies", companyRepository.findAll());
        return "company-select";
    }

    @GetMapping("/company/select/{id}")
    public String joinCompany(@PathVariable Long id, Model model) {
        
        model.addAttribute("company", companyRepository.findById(id).get());

        return "company-view";
    }

    @GetMapping("/company/join/{id}")
    public String joinCompany2(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal, Model model) {

        User user = userRepository.findByEmail(principal.getUsername());    //find the current user in the database

        // //TODO schöner lösen
        if(user.getCompany() != null) { //falls Student bereits zugeordnet, soll das nicht möglich sein  (GET..)
             return "redirect:/home";
        }
        user.setCompany(companyRepository.findById(id).get());  //set the company of that user.
        
        userRepository.save(user);

        return "redirect:/home";
    }


    @GetMapping("/company")
    public String viewOwnCompany(Model model, @AuthenticationPrincipal UserDetails principal) {

        User user = userRepository.findByEmail(principal.getUsername());    //find the current user in the database

        model.addAttribute("company", companyRepository.findById(user.getCompany().getId()).get());

        return "company-info";
    }

    @GetMapping("/company/edit")
    public String editOwnCompany(Model model, @AuthenticationPrincipal UserDetails principal) {

        User user = userRepository.findByEmail(principal.getUsername());    //find the current user in the database

        model.addAttribute("company", companyRepository.findById(user.getCompany().getId()).get());

        return "company-edit";
    }

    @PostMapping("/company/edit")
    public String saveOwnCompany(@Valid Company company, BindingResult bindingResult, @AuthenticationPrincipal UserDetails principal, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("company", company);
			return "company-edit";
		}
        User user = userRepository.findByEmail(principal.getUsername());    //find the current user in the database
        company.setId(user.getCompany().getId());                           //set the id of new Company-Object to the old id
        companyRepository.save(company);                                    //old company get overwritten, since id is primary key

        return "redirect:/company";
    }

}
