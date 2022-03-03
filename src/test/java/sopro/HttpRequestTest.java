package sopro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.validation.constraints.AssertTrue;

import org.assertj.core.api.Assert;

@SpringBootTest
@AutoConfigureMockMvc
public class HttpRequestTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests, if the login screen is served on /logi
     */
    @Test
    public void testLoginScreen() throws Exception {
        assertTrue(mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("Login"));
    }

    /**
     * Tests, if the client is redirected to login when accessing /companies as a
     * not authed user.
     */
    @Test
    public void testRedirectToLogin() throws Exception {
        assertEquals(mockMvc.perform(get("/companies"))
                .andExpect(status().is(302))
                .andReturn()
                .getResponse()
                .getRedirectedUrl(), "http://localhost/login"); // TODO server host not dynamic.
    }

    // @Test
    // public void getConsoleWithLoginAdmin() throws Exception{
    // ResultActions action =
    // mockMvc.perform(post("/console").with(user("admin").roles("ADMIN")));
    // int status = action.andReturn().getResponse().getStatus();
    // assertTrue("expected status code = 404 ; current status code = " + status,
    // status == 404);
    // }

}
