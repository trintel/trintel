package sopro;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Company;
import sopro.model.InitiatorType;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;



@SpringBootTest
@AutoConfigureMockMvc
public class TransactionTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ActionTypeRepository actionTypeRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    private MockMvc mockMvc;

    String companyName = "NewComp";
    
    // #######################################################################################
    // ----------------------------------- Method Tests
    // #######################################################################################
    
    /**
     * Tests if the view with the Transaction listed is shown for the admin.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void listTransactionsTestAdmin() throws Exception {
        mockMvc.perform(get("/transactions"))
               .andExpect(view().name("transactions-list"));
    }

    /**
     * Tests if the view with the Transaction listed is shown for the student.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void listTransactionsTestStudent() throws Exception {
        mockMvc.perform(get("/transactions"))
               .andExpect(view().name("transactions-list"));
    }

    /**
     * Tests if admin can create transactions for companies of users.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void createTransactionsTestAdmin() throws Exception {
        mockMvc.perform(get("/transaction/" + userRepository.findByEmail("j@j").getCompany().getId() + "/create"))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if user can create transactions for his company.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void createTransactionsTestStudent1() throws Exception {
        mockMvc.perform(get("/transaction/" + userRepository.findByEmail("j@j").getCompany().getId() + "/create"))
               .andExpect(status().isOk())
               .andExpect(view().name("transaction-add"));
    }

    /**
     * Tests if user can create transactions for another company.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void createTransactionsTestStudent2() throws Exception {
        mockMvc.perform(get("/transaction/" + userRepository.findByEmail("f@f").getCompany().getId() + "/create"))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if admin can not save transactions
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void saveTransactionsTestAdmin() throws Exception {
        
    }



    /**
     * Tests if student can save transactions.
     * @throws Exception
     */
  /*   @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void saveTransactionsTestStudent() throws Exception {
        
        ActionType request = new ActionType("Request", "Demo request text.", InitiatorType.BUYER);
        Transaction transaction = new Transaction(companyRepository.findById(userRepository.findByEmail("j@j").getCompany().getId()).get(), companyRepository.findById(userRepository.findByEmail("f@f").getCompany().getId()).get());
        Action transRequest = new Action("Test message", request, transaction);
        transaction.setProduct("Product 1");
        transRequest.setAmount(10);
        transRequest.setPricePerPiece(0.5); */

      /*   actionTypeRepository.save(request);
        transactionRepository.save(transaction);
        actionRepository.save(transRequest); */


        
        //transaction.setActions([action]);
        /*    

        mockMvc.perform(post("/transaction/" + companyRepository.findById(userRepository.findByEmail("j@j").getCompany().getId()) + "/save").flashAttr("action", transRequest).with(csrf())
                                                                                                                                            .flashAttr("transaction", transaction).with(csrf()))
               .andExpect(redirectedUrl("/transactions"));
    }

 */



    // #######################################################################################
    // ----------------------------------- Integration Tests
    // #######################################################################################               .andExpect(status().is3xxRedirection())




}
