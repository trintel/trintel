package sopro;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StatistikTest {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    ActionTypeRepository actionTypeRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    String companyName = "NewComp";

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
     * Tests if the view with the Statistik is shown for the admin.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void listStatistikTestAdmin() throws Exception {

        mockMvc.perform(get("/statistics/" + userRepository.findByEmail("j@j").getCompany().getId()))
               .andExpect(status().isOk())
               .andExpect(view().name("statistics-Student"));
    }

    /**
     * Tests if the view with the Statistik is shown for the student.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void listStatistikTestStudent1() throws Exception {

        mockMvc.perform(get("/statistics/" + userRepository.findByEmail("j@j").getCompany().getId()))
               .andExpect(status().isOk())
               .andExpect(view().name("statistics-Student"));
    }

    /**
     * Tests if the student can not see the statistics from other companies.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void listStatistikTestStudent2() throws Exception {

        mockMvc.perform(get("/statistics/" + userRepository.findByEmail("f@f").getCompany().getId()))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if the view with the Admin Statistik is shown for the admin.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void listAdminStatistikTestAdmin() throws Exception {

        mockMvc.perform(get("/statistics-Admin"))
               .andExpect(status().isOk())
               .andExpect(view().name("statistics-Admin"));
    }



    /**
     * Tests if the view with the Admin Statistik is not shown for the student.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void listAdminStatistikTestStudent() throws Exception {

        mockMvc.perform(get("/statistics-Admin"))
               .andExpect(status().isForbidden());
    }



}
