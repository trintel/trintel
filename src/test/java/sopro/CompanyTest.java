package sopro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests, if the Company screen is served on /myCompany.
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void adminAddCompany() throws Exception {
        mockMvc.perform(get("/companies"))
                .andExpect(status().is(200));

        mockMvc.perform(get("/companies/add"))
                .andExpect(status().is(200));

        mockMvc.perform(post("/companies/save").param("name", "187Comp.").with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/companies"))
                .andExpect(status().isFound());
    }
}
