package sopro;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import org.springframework.http.HttpMethod;

import sopro.model.Company;
import sopro.model.User;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;


import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.annotation.Retention;
import java.security.Principal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;

import javax.servlet.ServletContext;
import javax.validation.constraints.Null;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.aspectj.lang.annotation.Before;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;



@SpringBootTest
@AutoConfigureMockMvc
public class CompanyTest {
       
       @Autowired
       UserRepository userRepository;


       @Autowired
       CompanyRepository companyRepository;
       
       @Autowired
       ObjectMapper objectMapper;
       
       @Autowired
       private MockMvc mockMvc;
   
       String companyName = "NewComp";

// #######################################################################################
// ----------------------------------- Method Tests -------------------------------------
// --------------------------------------- ADMIN ----------------------------------------
// #######################################################################################
       
     /**
     * Tests if the right view is displayed when the Admin wants to see all Companies
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"})
    public void listCompaniesTestAdmin() throws Exception {

        mockMvc.perform(get("/companies"))
               .andExpect(status().isOk())
               .andExpect(view().name("company-list"));	
    }

    /**
     * Tests if the students can not see the list of all companys
     *  @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = {"STUDENT"})
    public void listCompaniesTestStudent() throws Exception {

        mockMvc.perform(get("/companies"))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if the Admin can acces the add view
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"})
    public void addCompanieTestAdmin() throws Exception {
        mockMvc.perform(get("/companies/add"))
               .andExpect(status().isOk());
    }

    /**
     * Tests if the Student can not acces the add view
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = {"STUDENT"})
    public void addCompanieTestStudent() throws Exception {
        mockMvc.perform(get("/companies/add"))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if the Admin can acces the view to save a company before he adds a company
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"})
    public void saveCompanieTestAdmin() throws Exception {
        mockMvc.perform(get("/companies/save"))
               .andExpect(status().is(405));         //Test with database check is down below
    }

    /**
     * Tests if the Student can not acces the view to save a company, and save it to the database
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = {"STUDENT"})
    public void saveCompanieTestStudent() throws Exception {
        mockMvc.perform(get("/companies/save"))
               .andExpect(status().isForbidden());
       
    }

    /**Test before function
     * Tests if the Admin can acces the list of all students
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"})
    public void listAllStudentsTestAdmin() throws Exception {
        mockMvc.perform(get("/students"))
               .andExpect(status().isOk())
               .andExpect(view().name("students-list"));	
    }
            
   /**Test before function
    * Tests if the Student can not acces the list of all students
    * @throws Exception
    */
    @Test
    @WithMockUser(username = "student@student", roles = {"STUDENT"})
    public void listAllStudentsTestStudent() throws Exception {
        mockMvc.perform(get("/students"))
               .andExpect(status().isForbidden());
    }
            
   /**
    * Tests if the Company page is reachable for the Admin
    * @throws Exception
    */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void companiesReachable() throws Exception {
        mockMvc.perform(get("/companies"))
               .andExpect(status().is(200));
    }


   // TODO Write Test for editStudent and moveToCompany when function implemented


// #######################################################################################
// ----------------------------------- Method Tests -------------------------------------
// ------------------------------------- Students ---------------------------------------
// #######################################################################################


    /**Test before function
     * Tests if the Admin can not acces the company selection page
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"})
    public void companySelectTestAdmin() throws Exception {
        mockMvc.perform(get("/company/select"))
               .andExpect(status().isForbidden());
    }

    /**Test before function
     * Tests if the Student can acces the company selection page
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = {"STUDENT"})
    public void companySelectTestStudent() throws Exception {
        mockMvc.perform(get("/company/select"))
               .andExpect(status().isOk())
               .andExpect(view().name("company-select"));	
    }

    /**
     * Tests if the Admin can not select a company
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"})
    public void companySelectIdTestAdmin() throws Exception {
        //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
              companyRepository.delete(companyRepository.findByName(companyName));
          }  
        Company testCompany = new Company(companyName); 
        companyRepository.save(testCompany);
        long id = testCompany.getId();   

        mockMvc.perform(get("/company/select/" + id))
               .andExpect(status().isForbidden());

        companyRepository.delete(testCompany);
        
    }

    /**
     * Tests if the Student can select the company
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = {"STUDENT"})
    public void companySelectIdTestStudent() throws Exception {
        //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
                companyRepository.delete(companyRepository.findByName(companyName));
            } 
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        long id = testCompany.getId();        

        mockMvc.perform(get("/company/select/" + id))
               .andExpect(status().isOk())
               .andExpect(view().name("company-view"));	

        companyRepository.delete(testCompany);
    }



   /////////////////////////////////////
   /// TODO Write IntegrationTest for joinCompany2



    /**
     * Tests if the Admin can not see his company (has none)
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"})
    public void viewOwnCompanyTestAdmin() throws Exception {
        mockMvc.perform(get("/company"))
               .andExpect(status().isForbidden());
    }
 
// TODO editOwnCompany und saveOwnCompany

  
    
// #######################################################################################
// ----------------------------------- Integration Tests ---------------------------------
// #######################################################################################

        
    /**
     * Tests, if the the admin can add a company.
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void adminAddCompany() throws Exception {
        Company testCompany = companyRepository.findByName(companyName);
        
        if (testCompany != null) {
            companyRepository.delete(testCompany);
            
        }

        mockMvc.perform(get("/companies/add"))
               .andExpect(status().is(200));

        mockMvc.perform(post("/companies/save").param("name", companyName).with(csrf()))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/companies"))
               .andExpect(status().isFound());

        assertNotEquals(null,companyRepository.findByName(companyName));

    }

<<<<<<< HEAD

    /**
     * Tests if the student can join a company
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("m@m")
    public void studentJoinCompany() throws Exception{

        mockMvc.perform(get("/company/select"))
               .andExpect(status().is(200))
               .andExpect(content().string(containsString(companyName)));

        Company company = companyRepository.findByName(companyName);
        long id = company.getId();

        mockMvc.perform(get("/company/select/" + id))
               .andExpect(status().isOk());


        mockMvc.perform(post("/company/join").param("companyName", companyName).with(csrf()))
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/home"));
                   
    }

    
=======
  
>>>>>>> f355a84d4aea718f5b8985e8b2fb303dbeedaa6e
    /**
     * Test written before Function implemented
     * Tests, if the added Company can be deleted
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void adminDeleteCompany() throws Exception {
        mockMvc.perform(get("/companies/delete"))
               .andExpect(status().is(200));   

        
        mockMvc.perform(post("/company/deleted").param("name", companyName).with(csrf()))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/companies"))
               .andExpect(status().isFound());
    }

}
