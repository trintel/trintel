package sopro.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.Company;
import sopro.repository.CompanyRepository;
@Controller
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;
    
    @GetMapping("/companies")
    public String listCompanies(Model model) {
        model.addAttribute("companies", companyRepository.findAll());
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

}
