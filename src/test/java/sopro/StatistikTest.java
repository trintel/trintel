package sopro;

import static org.mockito.ArgumentMatchers.contains;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Company;
import sopro.model.InitiatorType;
import sopro.model.Transaction;
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
    BeforeTest beforeTest;

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
        beforeTest.setup();
    }

    /**
     * Test before implementation    
     *
     * Tests if the view with the Statistik is shown for the admin.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void listStatistikTestAdmin() throws Exception {
        
        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            mockMvc.perform(get("/statistics/" + userRepository.findByEmail("admin@admin").getCompany()))
                   .andExpect(view().name("statistics"))
                   .andExpect(content().string(contains(company.getName())));
               }
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
               .andExpect(view().name("statistics"));
    }

    /**
     * Tests if the student can not see the statistics from other companies.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void listStatistikTestStudent2() throws Exception {
        
        mockMvc.perform(get("/statistics/" + userRepository.findByEmail("f@f").getCompany().getId()))
               .andExpect(status().is3xxRedirection()) 
               .andExpect(redirectedUrl("/home"));
    }
}