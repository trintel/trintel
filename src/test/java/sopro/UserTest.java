package sopro;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;

import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;
import sopro.service.SignupUrlInterface;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CompanyRepository companyRepository;


    @Autowired
    DatabaseService databaseService;

    @Autowired
    SignupUrlInterface signupUrlService;


    @BeforeTransaction
    void setup() {
        databaseService.clearDatabase();
        databaseService.setup();
    }

    @AfterTransaction
    void clean() {
        databaseService.clearDatabase();
    }

    /**
     * Tests, if the login screen is served on /signup/StudentUrl.
     *
     * @throws Exception
     */
    @Test
    public void signUpRedirectTestStudent() throws Exception {
        String generatedString = signupUrlService.generateStudentSignupURL();

        mockMvc.perform(get("/signup/" + generatedString))
               .andExpect(status().isOk())
               .andExpect(view().name("sign-up-student"));
    }

    /**
     * Tests, if the login screen is served on /signup/adminUrl.
     *
     * @throws Exception
     */
    @Test
    public void signUpRedirectTestAdmin() throws Exception {
        String generatedString = signupUrlService.generateAdminSignupURL();

        mockMvc.perform(get("/signup/" + generatedString))
               .andExpect(status().isOk())
               .andExpect(view().name("sign-up-admin"));
    }


    /**
     * Tests, if the login screen is served on /signup/falseURL.
     *
     * @throws Exception
     */
    @Test
    public void signUpRedirectTestFalseString() throws Exception {
        String test = "kjsadgusghsfgufhkjsdghushgughdagounadfh";

        mockMvc.perform(get("/signup/" + test))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }

    /**
     * Tests, if the login screen is served on /login.
     *
     * @throws Exception
     */
    @Test
    public void loginAvailableForAll() throws Exception {
        mockMvc
                .perform(get("/login"))
                .andExpect(status().isOk());
    }


    /**
     * Test if the Logout of the Admin works and if the admin is redirected to the
     * login page.
     *
     * @throws Exception
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
     * Test if the Logout of the Student works and he is redirected to the Login.
     * Page
     *
     * @throws Exception
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
     * Tests, if the user is redirected to login error page when he uses wrong.
     * password.
     *
     * @throws Exception
     */
    @Test
    public void testInvalidLoginDenied() throws Exception {
        mockMvc
                .perform(formLogin().password("dasisteinpasswort"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    /**
     * Tests, if the client is redirected to login when accessing /companies as a
     * not authed user.
     * Code 302 = Redirection to another page.
     *
     * @throws Exception
     */
    @Test
    public void testRedirectToLogin() throws Exception {
        assertTrue(mockMvc.perform(get("/companies"))
                .andExpect(status().is(302))
                .andReturn()
                .getResponse()
                .getRedirectedUrl().endsWith("/login"));
    }
}
