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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import sopro.model.Company;
import sopro.model.CompanyLogo;
import sopro.model.User;
import sopro.repository.CompanyLogoRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;

@Controller
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyLogoRepository companyLogoRepository;

    // #######################################################################################
    // ----------------------------------- ADMIN FUNCTIONS
    // #######################################################################################

    @GetMapping("/companies/add")
    public String addCompany(Model model) {
        Company company = new Company(); // creating a new Company Object to be added to the database
        model.addAttribute("company", company);
        return "company-create";
    }

    @PostMapping("/companies/save")
    public String saveCompany(@Valid Company company, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("company", company);
            return "company-create";
        }
        companyRepository.save(company); // saves the new Company
        return "redirect:/companies";
    }

    // TODO Rechte einschränken
    @PostMapping("/companies/delete/{companyID}")
    public String deleteCompany(@PathVariable Long companyID, Model model) {
        //TODO maybe delete all transactions. so that companies with transactions can also be deleted.
        if(companyLogoRepository.findByCompanyId(companyID) != null) {
            companyLogoRepository.delete(companyLogoRepository.findByCompanyId(companyID));     //company gets deleted because of cascade.remove.
        }

        companyRepository.deleteById(companyID);                                            //if there is no custom logo just delete the company
        return "redirect:/companies";
    }

    @GetMapping("/students") //TODO handle unassigned in Frontend
    public String listAllStudents(Model model) {
        // Company company = new Company("unassgined");
        // for (User student : userRepository.findByRole("STUDENT")) {
        //     if (student.getCompany() == null)
        //         student.setCompany(company);
        // }
        model.addAttribute("students", userRepository.findByRole("STUDENT")); // list all students
        return "students-list";
    }

    @GetMapping("/student/{id}/reassign")
    public String editStudent(Model model, @PathVariable Long id) {
        User student = userRepository.findById(id).get();
        model.addAttribute("student", student);
        if (student.getCompany() == null) {
            model.addAttribute("companies", companyRepository.findAll());
        } else {
            model.addAttribute("companies", companyRepository.findByIdNot(student.getCompany().getId())); // get all
                                                                                                          // companies
                                                                                                          // except for
                                                                                                          // the current
                                                                                                          // one
        }
        model.addAttribute("studentID", id); // add the student id to the model (for post-request navigation)
        return "student-reassign";
    }

    @PostMapping("/student/{id}/reassign")
    public String moveToCompany(String companyName, @PathVariable Long id, Model model) {
        User user = userRepository.findById(id).get(); // find the student to be editet
        Company company2 = companyRepository.findByName(companyName); // find the new company
        user.setCompany(company2);
        userRepository.save(user);
        return "redirect:/students";
    }

    @PreAuthorize("hasPermission(#companyID, 'company')")
    @GetMapping("/companies/{companyID}")
    public String viewCompany(@PathVariable Long companyID, Model model) {
        model.addAttribute("company", companyRepository.findById(companyID).get());
        return "company-info";
    }

    // #########################################################################################
    // ----------------------------------- STUDENT FUNCTIONS
    // #########################################################################################

    @GetMapping("/company/select")
    public String selectCompany(Model model) {
        // .findByOrderByNameAsc() statt .findAll()
        model.addAttribute("companies", companyRepository.findByOrderByNameAsc());
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
        // if(user.getCompany() != null) { //falls Student bereits zugeordnet, soll das
        // nicht möglich sein (GET..)
        // return "redirect:/home";
        // }
        user.setCompany(companyRepository.findByName(companyName)); // set the company of that user.
        userRepository.save(user);

        return "redirect:/home";
    }

    @GetMapping("/company")
    public String viewOwnCompany(Model model, @AuthenticationPrincipal User user) {
        return "redirect:/companies/" + user.getCompany().getId();
    }

    @PreAuthorize("hasPermission(#companyID, 'company')")
    @GetMapping("/company/{companyID}/edit")
    public String editOwnCompany(Model model, @AuthenticationPrincipal User user, @PathVariable Long companyID) {
        try {
            if (user.getRole().equals("ADMIN") || user.getCompany().getId().equals(companyID)) {
                model.addAttribute("company", companyRepository.findById(companyID).get());
                return "company-edit";
            } else {
                return "redirect:/home"; // not allowed to edit that company
            }
        } catch (NullPointerException e) { // if company not assigned (should never happen...)
            return "redirect:/home";
        }
    }

    @PostMapping(consumes = "multipart/form-data", value = "/company/{companyID}/edit")
    public String saveOwnCompany(@RequestParam("formFile") MultipartFile companyLogo, @Valid Company company,
            BindingResult bindingResult, @PathVariable Long companyID, @AuthenticationPrincipal User user, Model model)
            throws Exception {
        if (bindingResult.hasErrors()) {
            model.addAttribute("company", company);
            return "company-edit";
        }
        company.setId(companyID); // set the id of new Company-Object to the old id
        companyRepository.save(company); // old company get overwritten, since id is primary key

        if (!companyLogo.isEmpty()) {
            CompanyLogo dbLogo = companyLogoRepository.findByCompany(company);
            if (dbLogo == null) {
                dbLogo = new CompanyLogo();
            }
            dbLogo.setLogo(companyLogo.getBytes());
            dbLogo.setCompany(company);
            companyLogoRepository.save(dbLogo);
        }
        return "redirect:/companies/" + companyID;
    }

    // #########################################################################################
    // ----------------------------------- FUNCTIONS FOR BOTH
    // #########################################################################################

    // a list of all companies.
    // Admins can click on them an get redirected to: TODO list of students in
    // company (reassign)
    // Admins can add new Companies
    // Students see only other companies. can click on one to start transaction.
    @GetMapping("/companies")
    public String listCompanies(Model model, @AuthenticationPrincipal User user) {
        if (user.getRole().equals("ADMIN")) {
            model.addAttribute("companies", companyRepository.findAll()); // add a list of all companies to the model
        } else {
            model.addAttribute("companies", companyRepository.findByIdNot(user.getCompany().getId()));
            // add a list of all companies to the model, without the own company.
        }
        return "company-list";
    }
}
