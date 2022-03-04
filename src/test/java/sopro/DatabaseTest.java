package sopro;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import sopro.model.Company;
import sopro.model.User;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class DatabaseTest {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    CompanyRepository companyRepository;

    private User user;

    private Company company;

    String forename = "forename";
    String lastname = "lastname";
    String email = "email@email.com";
    String password = "password";
    
   

    /**
     * Creates a User and a Company in the database
     */
    @BeforeEach
    public void addUserandCompany() {
        user = new User(lastname, forename, email, password, null);
        user.setRole("STUDENT");                        //model -> user: Role not null
        company= new Company("company");
        companyRepository.save(company);
        user.setCompany(company);
        userRepository.save(user);
        
        }

    /**
     * Checks if a created user is really saved in database.
     */
    @Test
    public void isUserInDatabase() throws Exception{
        assertEquals(user, userRepository.findById(user.getId()).get());        //.get(): optional<User> in User
    }



    
}
