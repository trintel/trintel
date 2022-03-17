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

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Transaction;
import sopro.model.util.InitiatorType;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BackUpTest {
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
     * Tests if the Import / Export works
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void saveTransactionsTestAdmin() throws Exception {
        //exportieren
        mockMvc.perform(get("/backup/export"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/home"));

        //importieren
        mockMvc.perform(post("uriVars").param("path", "/cleanupBackups.sh").with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/home"));



    }
    
}
