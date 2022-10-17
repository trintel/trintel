package sopro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Company;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;
import sopro.service.backup.ExportInterface;
import sopro.service.backup.ImportInterface;

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
    PasswordEncoder passwordEncoder;

    @Autowired
    ImportInterface importService;

    @Autowired
    ExportInterface exportService;

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
    public void importExportTestAdmin() throws Exception {
        int UserBefore = 0;
        for (User user : userRepository.findAll()) {
            UserBefore += 1;
        }
        int CompaniesBefore = 0;
        for (Company company : companyRepository.findAll()) {
            CompaniesBefore += 1;
        }
        int ActionTypeBefore = 0;
        for (ActionType actionType : actionTypeRepository.findAll()) {
            ActionTypeBefore += 1;
        }
        int ActionBefore = 0;
        for (Action action : actionRepository.findAll()) {
            ActionBefore += 1;
        }
        int TransactionBefore = 0;
        for (Transaction transaction : transactionRepository.findAll()) {
            TransactionBefore += 1;
        }

        String path = exportService.export();
        databaseService.clearDatabase();
        importService.importJSON(path);

        int UserAfter = 0;
        for (User user : userRepository.findAll()) {
            UserAfter += 1;
        }
        int CompaniesAfter = 0;
        for (Company company : companyRepository.findAll()) {
            CompaniesAfter += 1;
        }
        int ActionTypeAfter = 0;
        for (ActionType actionType : actionTypeRepository.findAll()) {
            ActionTypeAfter += 1;
        }
        int ActionAfter = 0;
        for (Action action : actionRepository.findAll()) {
            ActionAfter += 1;
        }
        int TransactionAfter = 0;
        for (Transaction transaction : transactionRepository.findAll()) {
            TransactionAfter += 1;
        }

        assertEquals(UserBefore, UserAfter);
        assertEquals(CompaniesAfter, CompaniesBefore);
        assertEquals(ActionTypeBefore, ActionTypeAfter);
        assertEquals(ActionBefore, ActionAfter);
        assertEquals(TransactionBefore, TransactionAfter);
    }

}
