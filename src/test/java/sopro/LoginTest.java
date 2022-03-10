package sopro;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.web.servlet.MockMvc;

// @RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

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

    // TODO Rechteverwaltung?
    /**
     * Tests if the Admin can access the companies.
     *
     * @throws Exception
     */
    // @Test
    // @WithMockUser(username = "admin@admin", roles = { "ADMIN" }) // Erstellt User
    // ohne die Daternbank zu verwenden.
    // public void adminCanSeeCompanies() throws Exception {
    // mockMvc
    // .perform(get("/companies"))
    // .andExpect(status().is(200));
    // }

    /**
     * Test if the Logout of the Admin works and if the admin is redirected to the
     * login page
     *
     * @throcpti
     */
    @Test
    @AfterTestMethod
    public void adminLogout() throws Exception {
        mockMvc
                .perform(logout())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?logout"));
    }

    // TODO Rechteverwaltung anpassen.
    /**
     * Tests if the Student can access the companies.
     *
     * @throws Exception
     */
    // @Test
    // @WithMockUser(username = "student@student", roles = { "STUDENT" }) //
    // Erstellt User ohne die Daternbank zu
    // // verwenden.
    // public void studentCanNotSeeCompanies() throws Exception {
    // mockMvc
    // .perform(get("/companies"))
    // .andExpect(status().isForbidden());
    // }

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