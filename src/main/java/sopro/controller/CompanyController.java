package sopro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import sopro.repository.CompanyRepository;
@Controller
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;
    
}
