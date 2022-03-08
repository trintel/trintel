package sopro;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import sopro.model.Company;
import sopro.repository.CompanyRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        CompanyRepository companyRepository;
        String companyName = "NewComp";

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

                assertNotEquals(null, companyRepository.findByName(companyName));
        }
}
