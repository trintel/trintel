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
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import sopro.model.Action;
import sopro.model.ActionType;
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
public class TransactionTest {
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
     * Tests if user can create transactions with his own company.
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
     * Tests if user can create transactions.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void createTransactionsTestStudent2() throws Exception {
        mockMvc.perform(get("/transaction/" + userRepository.findByEmail("f@f").getCompany().getId() + "/create"))
               .andExpect(status().isOk())
               .andExpect(view().name("transaction-add"));
    }

    /**
     * Tests if student can save transactions.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void saveTransactionsTestStudent() throws Exception {
        //Create Transaction
        ActionType testActionType = new ActionType("TestActionType", "Demo request text.", InitiatorType.BUYER);
        testActionType.setStandartAction(true);
        
        Transaction testTransaction = new Transaction(companyRepository.findById(userRepository.findByEmail("j@j").getCompany().getId()).get(), companyRepository.findById(userRepository.findByEmail("f@f").getCompany().getId()).get());
        testTransaction.setProduct("Product 1");

        Action testAction = new Action("Test message testAction", testActionType, testTransaction);
        testAction.setInitiator(userRepository.findByEmail("j@j"));

        actionTypeRepository.save(testActionType);
        transactionRepository.save(testTransaction);

        Long companyIdSeller = userRepository.findByEmail("f@f").getCompany().getId();

        mockMvc.perform(post("/transaction/" + companyIdSeller + "/save").flashAttr("action", testAction).with(csrf()))                                                                                                                                                                                                                                                                    
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/transactions"));
    }

    /**
     * Tests if student can save transactions.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void saveTransactionsTestAdmin() throws Exception {
        //Create Transaction
        ActionType testActionType = new ActionType("TestActionType", "Demo request text.", InitiatorType.BUYER);
        testActionType.setStandartAction(true);
        
        Transaction testTransaction = new Transaction(companyRepository.findById(userRepository.findByEmail("j@j").getCompany().getId()).get(), companyRepository.findById(userRepository.findByEmail("f@f").getCompany().getId()).get());
        testTransaction.setProduct("Product 1");

        Action testAction = new Action("Test message testAction", testActionType, testTransaction);
        testAction.setInitiator(userRepository.findByEmail("j@j"));

        actionTypeRepository.save(testActionType);
        transactionRepository.save(testTransaction);

        Long companyIdSeller = userRepository.findByEmail("f@f").getCompany().getId();

        mockMvc.perform(post("/transaction/" + companyIdSeller + "/save").flashAttr("action", testAction).with(csrf()))                                                                                                                                                                                                                                                                    
               .andExpect(status().isForbidden());
    }
    
    


    /**Test before implementation funktioniert auch in der Ausführung nicht
     * Tests if user can see the details of a transaction.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void transactionDetailTestStudent() throws Exception {
        Long transactionId = transactionRepository.findByBuyer(userRepository.findByEmail("j@j").getCompany()).get(0).getId();
        mockMvc.perform(get("/transaction/" + transactionId))
               .andExpect(status().isOk())
               .andExpect(view().name("transaction-view"));

    }

    /**Test before implementation
     * Tests if admin can see the details of a transaction.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void transactionDetailTestAdmin() throws Exception {
        Long transactionId = transactionRepository.findByBuyer(userRepository.findByEmail("j@j").getCompany()).get(0).getId();
        mockMvc.perform(get("/transaction/" + transactionId))
               .andExpect(status().isForbidden());

    }

    /**
     * Tests if user can add an action to an transaction.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void createActionTestStudent() throws Exception {
        Transaction transaction = transactionRepository.findByBuyer(userRepository.findByEmail("j@j").getCompany()).get(0);

        ActionType request = new ActionType("offer", "testText", InitiatorType.BUYER);
        Action actionTest = new Action("Test message", request, transaction);

        actionTypeRepository.save(request);
        //actionRepository.save(actionTest);

        mockMvc.perform(post("/transaction/" + transaction.getId() + "/addAction").flashAttr("action", actionTest).with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/transaction/" + transaction.getId()));
    }


    /**
     * Tests if admin can not add an action to an transaction.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void createActionTestAdmin() throws Exception {
        Transaction transaction = transactionRepository.findByBuyer(userRepository.findByEmail("j@j").getCompany()).get(0);

        ActionType request = new ActionType("offer", "testText", InitiatorType.BUYER);
        Action actionTest = new Action("Test message", request, transaction);

        actionTypeRepository.save(request);
        //actionRepository.save(actionTest);

        mockMvc.perform(post("/transaction/" + transaction.getId() + "/addAction").flashAttr("action", actionTest).with(csrf()))
               .andExpect(status().isForbidden());
    }


    /**
     * Tests if student can see all actionstypes.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void showActionTypesTestStudent() throws Exception {
        mockMvc.perform(get("/actions"))
               .andExpect(status().isOk())
               .andExpect(view().name("action-list"));
    }

    /**
     * Tests if admin can see all actionstypes.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void showActionTypesTestAdmin() throws Exception {
        mockMvc.perform(get("/actions"))
               .andExpect(status().isOk())
               .andExpect(view().name("action-list"));
    }

    /**
     * Tests if student can add an actionstype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void addActionTypeTestStudent() throws Exception {
        mockMvc.perform(get("/action/add"))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if admin can add an actionstype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void addActionTypeTestAdmin() throws Exception {
        mockMvc.perform(get("/action/add"))
               .andExpect(status().isOk())
               .andExpect(view().name("action-add"));
    }


    /**
     * Tests if student can not save a new actiontype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void saveActionTypeTestStudent() throws Exception {

        ActionType actionType = new ActionType("neuerTyp", "testTextNeu", InitiatorType.BUYER);

        mockMvc.perform(post("/action/save").flashAttr("actionType", actionType).with(csrf()))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if admin can save a new actiontype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void saveActionTypeTestAdmin() throws Exception {

        ActionType actionType = new ActionType("neuerTyp", "testTextNeu", InitiatorType.BUYER);

        mockMvc.perform(post("/action/save").flashAttr("actionType", actionType).with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/actions"));
    }


   /**
     * Tests if student can not edit an actiontype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void editExistingActionTypeTestStudent() throws Exception {

        Iterable<ActionType> actionTypes =  actionTypeRepository.findAll();
        long actionTypeId = actionTypes.iterator().next().getId();

        mockMvc.perform(get("/action/edit/" + actionTypeId))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if student can not edit an actiontype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void editExistingActionTypeTestAdmin() throws Exception {

        Iterable<ActionType> actionTypes =  actionTypeRepository.findAll();
        long actionTypeId = actionTypes.iterator().next().getId();

        mockMvc.perform(get("/action/edit/" + actionTypeId))
               .andExpect(status().isOk())
               .andExpect(view().name("action-edit"));
    }


    /**
     * Tests if student can not edit an actiontype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void editNewActionTypeTestStudent() throws Exception {
        ActionType actionType = new ActionType("neuerEditTyp", "TestTextNeu", InitiatorType.BUYER);
        actionTypeRepository.save(actionType);

        mockMvc.perform(get("/action/edit/" + actionType.getId()))
               .andExpect(status().isForbidden());
    }

    /**
     * Tests if admin can edit an actiontype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void editNewActionTypeTestAdmin() throws Exception {
        ActionType actionType = new ActionType("neuerEditTyp", "TestTextNeu", InitiatorType.BUYER);
        actionTypeRepository.save(actionType);

        mockMvc.perform(get("/action/edit/" + actionType.getId()))
               .andExpect(status().isOk())
               .andExpect(view().name("action-edit"));
    }


    /**
     * Tests if student can not edit and save an actiontype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void saveEditActionTypeTestStudent() throws Exception {

        Iterable<ActionType> actionTypes =  actionTypeRepository.findAll();
        ActionType actionType = actionTypes.iterator().next();
        long actionTypeId = actionType.getId();

        mockMvc.perform(post("/action/edit/" + actionTypeId).flashAttr("actionType", actionType))
               .andExpect(status().isForbidden());
    }


    /**
     * Tests if admin can edit and save an actiontype.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void saveEditActionTypeTestAdmin() throws Exception {

        Iterable<ActionType> actionTypes =  actionTypeRepository.findAll();
        ActionType actionType = actionTypes.iterator().next();
        long actionTypeId = actionType.getId();

        mockMvc.perform(post("/action/edit/" + actionTypeId).flashAttr("actionType", actionType).with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/actions"));
    }

}
