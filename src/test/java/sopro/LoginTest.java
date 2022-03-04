package sopro;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import sopro.model.User;
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

// @RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {


    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests, if the login screen is served on /login
     */
    @Test
    public void loginAvailableForAll() throws Exception {
        mockMvc
                .perform(get("/login"))
                .andExpect(status().isOk());
    }

    /**
     * @throws Exception 
     * Tests if the Admnin can acces the companies
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"}) // Erstellt User ohne die Daternbank zu verwenden
    public void adminCanSeeCompanies() throws Exception {
        mockMvc
                .perform(get("/companies"))
                .andExpect(status().isOk());
        }
    
    /**
     * Test if the Logout of the Admin works
     */    
    @Test
    @AfterTestMethod
    public void adminLogout() throws Exception {
        mockMvc 
                .perform(logout())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?logout"));
    }

     /**
     * @throws Exception 
     * Tests if the Student can acces the companies
     */
    @Test
    @WithMockUser(username = "student@student", roles = {"STUDENT"}) // Erstellt User ohne die Daternbank zu verwenden
    public void studentCanNotSeeCompanies() throws Exception {
        mockMvc
                .perform(get("/companies"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/company/select"));
        }
    /**
     * Test if the Logout of the Student works
     */    
    @Test
    @AfterTestMethod
    public void studentLogout() throws Exception {
        mockMvc 
                .perform(logout())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?logout"));
    }


    /** 
     *  Tests, if the user is redirected to login error page when he uses wrong password
     */
    @Test
    public void testInvalidLoginDenied() throws Exception {
        String loginErrorUrl = "/login?error";             
        mockMvc
                .perform(formLogin().password("invalid"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(loginErrorUrl))
                .andExpect(unauthenticated());

        mockMvc
                .perform(get(loginErrorUrl))
                .andExpect(content().string(containsString("Invalid username and password")));
    }

    /**
     * Tests, if the client is redirected to login when accessing /companies as a
     * not authed user.
     * Code 302 = Redirection to another page
     */
    @Test
    public void testRedirectToLogin() throws Exception {
        assertTrue(mockMvc.perform(get("/companies"))
                .andExpect(status().is(302))
                .andReturn()
                .getResponse()
                .getRedirectedUrl().endsWith("/login"));
    }


    /**
     * @throws Exception
     * Test if the Sign Up page is reachable for the Students
     */
    @Test
    public void testSignupPageForStudents() throws Exception{
        mockMvc
                .perform(get("/signup/student"))
                .andExpect(status().isOk());
    }
    
    /**
     * @throws Exception
     * Test if the Sign Up page is reachable for the Admins
     */
    @Test
    public void testSignupPageForAdmin() throws Exception{
        mockMvc
                .perform(get("/signup/admin"))
                .andExpect(status().isOk());
    }

    

}
