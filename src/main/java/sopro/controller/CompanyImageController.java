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
    public void showProductImage(@PathVariable Long companyID, HttpServletResponse response) throws IOException {
        response.setContentType("image/png"); // Or whatever format you wanna use
        //TODO default Image. If there is no custom logo. Now: NullPointerException.
        CompanyLogo logo = companyLogoRepository.findByCompany(companyRepository.findById(companyID).get());

        InputStream is = new ByteArrayInputStream(logo.getLogo());
        IOUtils.copy(is, response.getOutputStream());
    }

}
