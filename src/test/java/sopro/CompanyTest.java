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

    //private MockHttpServletRequestBuilder builder;
    //private final ServletContext servletContext = new MockServletContext();


    String companyName = "NewComp";






    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests if the Company page is reachjable for the Admin
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void companiesReachable() throws Exception {
        mockMvc.perform(get("/companies"))
               .andExpect(status().is(200));
    }
        


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

    /**
     * 
     * @throws Exception
     */
/**   
    @Test
    @WithMockUser(username = "m@m", roles = { "STUDENT" })
    public void studentRegistrationDirectedToCompany() throws Exception {

        mockMvc.perform(formLogin().password("password").user("m@m"))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/home"));

        mockMvc.perform(get("/home"))                                       //Problem: UserController setHome2() wird auf einen "richtigen" User aufgerufen, funktioniert nicht mit MockUser
               .andExpect(authenticated())
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/company/select"));
    }
    
    /**
     * Tests if the student can join a company
     *
     * @throws Exception
     */
/** @Test
    @WithMockUser(username = "m@m", roles = { "STUDENT" })
    public void studentJoinCompany() throws Exception{
        User user = new User("student", "student","student@student", "password", null);

        mockMvc.perform(get("/company/select"))
               .andExpect(status().is(200))
               .andExpect(content().string(containsString(companyName)));

        Company company = companyRepository.findByName(companyName);
        long id = company.getId();

        mockMvc.perform(get("/company/select/" + id))
               .andExpect(status().isOk());

        mockMvc.perform(post("/company/join").content(objectMapper.writeValueAsString(user)).param("companyName", companyName).with(csrf()))
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/home"));
                   
    }

    @Test
    @WithMockUser(username = "student@student", roles = { "STUDENT" })
    public void studentJoinCompany2() throws Exception{
        

        mockMvc.perform(formLogin().password("password").user("m@m"))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/home"));             
        
        mockMvc.perform(get("/company/select"))
               .andExpect(status().is(200))
               .andExpect(content().string(containsString(companyName)));

        Company company = companyRepository.findByName(companyName);
        long id = company.getId();

        mockMvc.perform(get("/company/select/" + id))
               .andExpect(status().isOk());


        mockMvc.perform(post("/company/join").param("companyName", companyName).with(user("m@m").roles("STUDENT")).with(csrf()))
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/home"));
                   
    }
*/    


}
