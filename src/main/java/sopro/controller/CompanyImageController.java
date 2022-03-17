package sopro.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.CompanyLogo;
import sopro.repository.CompanyLogoRepository;
import sopro.repository.CompanyRepository;

@Controller
public class CompanyImageController {

    @Autowired
    CompanyLogoRepository companyLogoRepository;

    @Autowired
    CompanyRepository companyRepository;

    @GetMapping("/company/logo/{companyID}")
    public void showCompanyImage(@PathVariable Long companyID, HttpServletResponse response) throws IOException {
        response.setContentType("image/png"); // Or whatever format you wanna use
        CompanyLogo logo;
        logo = companyLogoRepository.findByCompany(companyRepository.findById(companyID).get());
        if(logo == null) {
            logo = companyLogoRepository.findByCompanyId(null);     // get the default logo with companyid null if no logo is found for the company
        }

        InputStream is = new ByteArrayInputStream(logo.getLogo());
        IOUtils.copy(is, response.getOutputStream());
    }

    //TODO: Security
    @PostMapping("/company/logo/{companyID}/delete")
    public String deleteCompanyLogo(@PathVariable Long companyID) {
        if(companyLogoRepository.findByCompanyId(companyID) != null) {
            companyLogoRepository.delete(companyLogoRepository.findByCompanyId(companyID));
        }
        return "redirect:/companies/" + companyID;
    }

}
