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

/*         @Autowired
        InitDatabaseService initDatabaseService;
    
    @BeforeAll
    public void initDatabase() {
        initDatabaseService.init();
    } */

// #######################################################################################
// ----------------------------------- Method Tests -------------------------------------
// --------------------------------------- ADMIN ----------------------------------------
// #######################################################################################

    /**
    * Tests if the Company page is reachable for the Admin
    * @throws Exception
    */
    @Test
    @WithUserDetails(value="admin@admin", userDetailsServiceBeanName="userDetailsService")
    public void companiesReachable() throws Exception {
        mockMvc.perform(get("/companies"))
               .andExpect(status().is(200));
    }

     /**
     * Tests if the right view is displayed when the Admin wants to see all Companies
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="admin@admin", userDetailsServiceBeanName="userDetailsService")
    public void listCompaniesTestAdmin() throws Exception {
        mockMvc.perform(get("/companies"))
               .andExpect(status().isOk())
               .andExpect(view().name("company-list"));	
    }

    /**
     * Tests if the students can see the list of all companys for transactions
     *  @throws Exception
     */
    @Test
    @WithUserDetails(value="j@j", userDetailsServiceBeanName="userDetailsService")
    public void listCompaniesTestStudent() throws Exception {
        mockMvc.perform(get("/companies"))
               .andExpect(status().isOk())
               .andExpect(view().name("company-list"));	
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
         //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
                companyRepository.delete(companyRepository.findByName(companyName));
            } 
        Company testCompany = new Company(companyName);
        mockMvc.perform(post("/companies/save").flashAttr("company", testCompany).with(csrf()))
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/companies"));         //Test with database check is down below
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
     * Tests if the student can be reassigned by the admin 
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "adminTest@admin", roles = {"ADMIN"})
    public void editReassignStudentTestAdmin() throws Exception {
         //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
            companyRepository.delete(companyRepository.findByName(companyName));
        }
        //Deletes User if it is allready in the database for some reason  
        if (userRepository.findByEmail("adminTest@admin") != null) {
            userRepository.delete(userRepository.findByEmail("adminTest@admin"));
        } 
                
        User testUser = new User("adminTest", "adminTest", "adminTest@admin", "password", null);
        testUser.setRole("ADMIN");
        userRepository.save(testUser);
        long id = testUser.getId();
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);


        mockMvc.perform(get("/student/" + id + "/reassign"))
               .andExpect(view().name("student-reassign"));

    }
    
    /**
     * Tests if the student can be reassigned by himself
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "studentTest@student", roles = {"STUDENT"})
    public void editReassignStudentTestStudent() throws Exception {
        //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
            companyRepository.delete(companyRepository.findByName(companyName));
        }
        //Deletes User if it is allready in the database for some reason  
        if (userRepository.findByEmail("studentTest@student") != null) {
            userRepository.delete(userRepository.findByEmail("studentTest@student"));
        } 
                
        User testUser = new User("studentTest", "studentTest", "studentTest@student", "password", null);
        testUser.setRole("STUDENT");
        userRepository.save(testUser);
        long id = testUser.getId();
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);

        mockMvc.perform(get("/student/" + id + "/reassign"))
               .andExpect(status().isForbidden());
    }


    /**
     * Tests if the student can be moved to another company by admin
     *
     * @throws Exception
     */        
    @Test
    @WithMockUser(username = "adminTest@admin", roles = {"ADMIN"})
    public void moveToCompanyTestAdmin() throws Exception {
        //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
            companyRepository.delete(companyRepository.findByName(companyName));
        }
        //Deletes User if it is allready in the database for some reason  
        if (userRepository.findByEmail("adminTest@admin") != null) {
            userRepository.delete(userRepository.findByEmail("adminTest@admin"));
        } 
               
        User testUser = new User("adminTest", "adminTest", "adminTest@admin", "password", null);
        testUser.setRole("ADMIN");
        userRepository.save(testUser);
        long id = testUser.getId();
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);

        mockMvc.perform(post("/student/"+ id +"/reassign").param("name", companyName).with(csrf()))
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/students"));
   
    }


     /**
     * Tests if the student can be moved to another company by himself
     *
     * @throws Exception
     */        
    @Test
    @WithMockUser(username = "studentTest@student", roles = {"STUDENT"})
    public void moveToCompanyTestStudent() throws Exception {
        //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
            companyRepository.delete(companyRepository.findByName(companyName));
        }
        //Deletes User if it is allready in the database for some reason  
        if (userRepository.findByEmail("studentTest@student") != null) {
            userRepository.delete(userRepository.findByEmail("studentTest@student"));
        } 
                
        User testUser = new User("studentTest", "studentTest", "studentTest@student", "password", null);
        testUser.setRole("STUDENT");
        userRepository.save(testUser);
        long id = testUser.getId();
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);

        mockMvc.perform(post("/student/"+ id +"/reassign").param("name", companyName).with(csrf()))
                .andExpect(status().isForbidden());

    }




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


    /**
     * Tests if the Admin can not join a company
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="admin@admin", userDetailsServiceBeanName="userDetailsService")
    public void joinCompany2TestAdmin() throws Exception {
        mockMvc.perform(post("/company/join").param("name", companyName).with(csrf()))
               .andExpect(status().isForbidden());

    }



    /**
     * Tests if the Student can join a company
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="student@student", userDetailsServiceBeanName="userDetailsService")
    public void joinCompany2TestStudent() throws Exception {
        mockMvc.perform(post("/company/join").param("name", companyName).with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/home"));
    }
 

    /**
     * Tests if the Admin can not edit his own company (has none)
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="admin@admin", userDetailsServiceBeanName="userDetailsService")
    public void editOwnCompanyAdmin() throws Exception {
        //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
                companyRepository.delete(companyRepository.findByName(companyName));
            } 
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        long id = testCompany.getId(); 

        
        mockMvc.perform(get("/company/" + id + "/edit"))
               .andExpect(status().isOk())
               .andExpect(view().name("company-edit"));
    }

    /**
     * Tests if the Admin can not edit his own company (has none)
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="studentTest@student", userDetailsServiceBeanName="userDetailsService")
    public void editOwnCompanyStudent() throws Exception {
        //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
                companyRepository.delete(companyRepository.findByName(companyName));
            } 
        if (userRepository.findByEmail("studentTest@student") != null) {
            userRepository.delete(userRepository.findByEmail("studentTest@student"));
            }       
            
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        
        User testUser = new User("studentTest", "studentTest", "studentTest@student", "password", testCompany);
        testUser.setRole("STUDENT");       
        userRepository.save(testUser);

        long id = testUser.getCompany().getId(); 
        
        mockMvc.perform(get("/company/" + id + "/edit"))
               .andExpect(status().isOk())
               .andExpect(view().name("company-edit"));
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

  
    /**
     * Test written before Function implemented
     * Tests, if the added Company can be deleted
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void adminDeleteCompany() throws Exception {
         //Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null) {
                companyRepository.delete(companyRepository.findByName(companyName));
            } 
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        long id = testCompany.getId();   

        mockMvc.perform(post("/companies/delete/" + id).param("name", companyName).with(csrf()))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/companies"))
               .andExpect(status().isFound());
    }

}
