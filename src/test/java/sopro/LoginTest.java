package sopro;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.nio.charset.Charset;
import java.util.Random;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;

import sopro.repository.UserRepository;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    BeforeTest beforeTest;

    @BeforeTransaction
    void setup() {
        beforeTest.setup();
    }

    //TODO: Error in User Controller getAdminUrl??? Luca fragen
    /**
     * Tests, if the login screen is served on /signup/randomUrl
     *
     * @throws Exception
     */
    @Test
    public void signUpRedirectTest() throws Exception {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        
        mockMvc.perform(get("/signup/" + generatedString))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }


    /**
     * Tests if a Student can Sign Up
     *
     * @throws Exception
     */
    // @Test
    // @WithMockUser(username = "test@test", roles = { "Student" })
    // public void signUpRoleAdminTest() throws Exception {
    //     mockMvc.perform(post("/companies/student").param("forename", "testVorname")
    //                                               .param("surname", "testSurname")
    //                                               .param("email", "testEmail@test")
    //                                               .param("password", "testPassword"))
    //            .andExpect(view().name("verify-your-email"));
    // }



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
     * login page
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
     * Test if the Logout of the Student works and he is redirected to the Login
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
     * Tests, if the user is redirected to login error page when he uses wrong
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
     * Code 302 = Redirection to another page
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
