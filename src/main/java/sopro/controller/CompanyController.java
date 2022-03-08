package sopro.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    //TODO Rechte einschränken
    @PostMapping("/companies/delete/{companyID}")
    public String deleteCompany(@PathVariable Long companyID, Model model) {

        companyRepository.deleteById(companyID);

        return "redirect:/companies";
    }

    @GetMapping("/students")
    public String listAllStudents(Model model) {
        model.addAttribute("students", userRepository.findByRole("STUDENT")); //list all students
        //TODO deal with unassigned students (null-pointer Except. in template)
        return "students-list";
    }

    @GetMapping("/student/{id}/reassign")
    public String editStudent(Model model, @PathVariable Long id) {
        User student = userRepository.findById(id).get();
        model.addAttribute("companies", companyRepository.findByIdNot(student.getCompany().getId())); //get all companies except for the current one
        model.addAttribute("studentID", id);                  //add the student id to the model (for post-request navigation)
        return "student-reassign";
    }

    @PostMapping("/student/{id}/reassign")
    public String moveToCompany(String companyName, @PathVariable Long id, Model model) {
        User user = userRepository.findById(id).get();      //find the student to be editet
        Company company2 = companyRepository.findByName(companyName);   //find the new company
        user.setCompany(company2);
        userRepository.save(user);
        return "redirect:/students";
    }

    //TODO Rechte einschränken
    @GetMapping("/companies/{companyID}")
    public String viewCompany(@PathVariable Long companyID, Model model) {
        model.addAttribute("company", companyRepository.findById(companyID).get());

        return "company-info";
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

    @PostMapping("/company/join") 
    public String joinCompany2(String companyName, @AuthenticationPrincipal User user, Model model) {

        // // //TODO schöner lösen
        // if(user.getCompany() != null) { //falls Student bereits zugeordnet, soll das nicht möglich sein  (GET..)
        //      return "redirect:/home";
        // }
        user.setCompany(companyRepository.findByName(companyName));  //set the company of that user.
        
        userRepository.save(user);

        return "redirect:/home";
    }


    @GetMapping("/company")
    public String viewOwnCompany(Model model, @AuthenticationPrincipal User user) {

        return "redirect:/companies/" + user.getCompany().getId();
    }

    @GetMapping("/company/edit")
    public String editOwnCompany(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("company", companyRepository.findById(user.getCompany().getId()).get());

        return "company-edit";
    }

    @PostMapping("/company/edit")
    public String saveOwnCompany(@Valid Company company, BindingResult bindingResult, @AuthenticationPrincipal User user, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("company", company);
			return "company-edit";
		}
        company.setId(user.getCompany().getId());                           //set the id of new Company-Object to the old id
        companyRepository.save(company);                                    //old company get overwritten, since id is primary key

        return "redirect:/company";
    }

// #########################################################################################
// ----------------------------------- FUNCTIONS FOR BOTH ----------------------------------
// #########################################################################################

    //a list of all companies. 
    //Admins can click on them an get redirected to: TODO list of students in company (reassign)
    //Admins can add new Companies
    //Students see only other companies. can click on one to start transaction.
    @GetMapping("/companies")   
    public String listCompanies(Model model, @AuthenticationPrincipal User user) {
        if(user.getRole().equals("ADMIN")) {
            model.addAttribute("companies", companyRepository.findAll()); //add a list of all companies to the model
        } else {
            model.addAttribute("companies", companyRepository.findByIdNot(user.getCompany().getId()));  //add a list of all companies to the model, without the own company.
        }
        return "company-list";
    }

}
