package sopro.controller;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import sopro.repository.RatingRepository;
import sopro.repository.UserRepository;

@Controller
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyLogoRepository companyLogoRepository;

    @Autowired
    RatingRepository ratingRepository;

    // #######################################################################################
    // ----------------------------------- ADMIN FUNCTIONS
    // #######################################################################################

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/companies/add")
    public String addCompany(Model model) {
        Company company = new Company(); // creating a new Company Object to be added to the database
        model.addAttribute("company", company);
        return "company-create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/companies/save")
    public String saveCompany(@Valid Company company, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("company", company);
            return "company-create";
        }
        companyRepository.save(company); // saves the new Company
        return "redirect:/companies";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/companies/delete/{companyID}")
    public String deleteCompany(@PathVariable Long companyID, Model model) {
        //TODO maybe delete all transactions. so that companies with transactions can also be deleted.
        if(companyLogoRepository.findByCompanyId(companyID) != null) {
            companyLogoRepository.delete(companyLogoRepository.findByCompanyId(companyID));     //company gets deleted because of cascade.remove.
        }
        companyRepository.deleteById(companyID);                                            //if there is no custom logo just delete the company
        return "redirect:/companies";
    }

    @GetMapping("/companies/{companyID}")
    public String viewCompany(@PathVariable Long companyID, Model model) {
        Company c = companyRepository.findById(companyID).get();
        model.addAttribute("company", c);
        Optional<Double> avg = ratingRepository.getAverageById(companyID);
        if(avg.isPresent()) {
            DecimalFormat df = new DecimalFormat("#.#");
            model.addAttribute("avgRating", df.format(avg.get()));
            model.addAttribute("starType", Math.round(avg.get()));
        }
        return "company-info";
    }

    // #########################################################################################
    // ----------------------------------- STUDENT FUNCTIONS
    // #########################################################################################

    @PreAuthorize("!hasCompany() and hasRole('STUDENT')")      //only if the company is not assigned and the user is student
    @GetMapping("/company/select")
    public String selectCompany(Model model) {
        // .findByOrderByNameAsc() statt .findAll()
        model.addAttribute("companies", companyRepository.findByOrderByNameAsc());
        return "company-select";
    }

    @PreAuthorize("!hasCompany() and hasRole('STUDENT')")
    @GetMapping("/company/select/{id}")
    public String joinCompany(@PathVariable Long id, Model model) {
        model.addAttribute("company", companyRepository.findById(id).get());
        return "company-view";
    }

    @PreAuthorize("!hasCompany() and hasRole('STUDENT')")
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

    @PreAuthorize("hasCompany()")   //to prevent NullPointer. Admins not allowed.
    @GetMapping("/company")
    public String viewOwnCompany(Model model, @AuthenticationPrincipal User user) {
        return "redirect:/companies/" + user.getCompany().getId();
    }

    @PreAuthorize("hasRole('ADMIN') or isInCompany(#companyID)")
    @GetMapping("/company/{companyID}/edit")
    public String editOwnCompany(Model model, @PathVariable Long companyID) {
        model.addAttribute("company", companyRepository.findById(companyID).get());
        return "company-edit";
    }

    @PreAuthorize("hasRole('ADMIN') or isInCompany(#companyID)")
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
        List<String> allowedFileTypes = Arrays.asList("image/png", "image/jpeg", "image/jpg");
        if (!companyLogo.isEmpty() && allowedFileTypes.contains(companyLogo.getContentType())) {
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
    //TODO difference between student and admin refactoring
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
