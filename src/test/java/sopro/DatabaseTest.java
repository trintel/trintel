package sopro;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Company;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.model.util.InitiatorType;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;

@SpringBootTest
@Transactional
public class DatabaseTest {


    @Autowired
    ActionTypeRepository actionTypeRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    DatabaseService databaseService;

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
     * Checks if a created user is really saved in database.
     *
     * @throws Exception
     */
    @Test
    public void isUserInDatabase() throws Exception {
        User testUser = new User(true, true, true, true, "testUser", "testUser", "testUser@testUser", "password", null);
        testUser.setRole("STUDENT");
        userRepository.save(testUser);
        User testUser2 = userRepository.findById(testUser.getId()).get();
        assertEquals(testUser, testUser2); // .get(): optional<User> in User
    }


    /**
     * Checks if the created user is assigned to the right company.
     *
     * @throws Exception
     */
    @Test
    public void isUserInCompany() throws Exception {
        Company testCompany = new Company("testCompany");
        User testUser = new User(true, true, true, true, "testUser", "testUser", "testUser@testUser", "password", testCompany);
        companyRepository.save(testCompany);
        testUser.setRole("STUDENT");
        userRepository.save(testUser);

        assertEquals(testCompany, userRepository.findById(testUser.getId()).get().getCompany());
    }

    /**
     * Tests if the company is saved in database.
     * @throws Exception
     */
    @Test
    public void isCompanyInDatabase() throws Exception {
        Company testCompany = new Company("testCompany");
        companyRepository.save(testCompany);

        assertEquals(testCompany, companyRepository.findById(testCompany.getId()).get());
    }


    /**
     * Tests if the transaction is saved in database.
     * @throws Exception
     */
    @Test
    public void isTransactionInDatabase() throws Exception {
        Company testCompany1 = new Company("testCompany1");
        companyRepository.save(testCompany1);
        Company testCompany2 = new Company("testCompany2");
        companyRepository.save(testCompany2);
        Transaction testTransaction = new Transaction(testCompany1, testCompany2);
        transactionRepository.save(testTransaction);

        assertEquals(testTransaction, transactionRepository.findById(testTransaction.getId()).get());
    }

    /**
     * Tests if the transaction is related to right companies in database.
     * @throws Exception
     */
    @Test
    public void isTransactionInDatabaseRelated() throws Exception {
        Company testCompanyBuyer = new Company("testCompanyBuyer");
        companyRepository.save(testCompanyBuyer);
        Company testCompanySeller = new Company("testCompanySeller");
        companyRepository.save(testCompanySeller);

        Transaction testTransaction = new Transaction(testCompanyBuyer, testCompanySeller);
        transactionRepository.save(testTransaction);

        assertEquals(testCompanyBuyer, transactionRepository.findById(testTransaction.getId()).get().getBuyer());
        assertEquals(testCompanySeller, transactionRepository.findById(testTransaction.getId()).get().getSeller());
    }


    /**
     * Tests if the confiormed is saved in the Database.
     * @throws Exception
     */
    @Test
    public void isConfirmedSetInDatabase() throws Exception {
        Company testCompanyBuyer = new Company("testCompanyBuyer");
        companyRepository.save(testCompanyBuyer);
        Company testCompanySeller = new Company("testCompanySeller");
        companyRepository.save(testCompanySeller);

        Transaction testTransaction = new Transaction(testCompanyBuyer, testCompanySeller);
        testTransaction.setConfirmed(true);
        transactionRepository.save(testTransaction);
        assertTrue(transactionRepository.findById(testTransaction.getId()).get().getConfirmed());
    }

    /**
     * Tests if the Paid is saved in the Database.
     * @throws Exception
     */
    @Test
    public void isPaidSetInDatabase() throws Exception {
        Company testCompanyBuyer = new Company("testCompanyBuyer");
        companyRepository.save(testCompanyBuyer);
        Company testCompanySeller = new Company("testCompanySeller");
        companyRepository.save(testCompanySeller);

        Transaction testTransaction = new Transaction(testCompanyBuyer, testCompanySeller);
        testTransaction.setPaid(true);
        transactionRepository.save(testTransaction);
        assertTrue(transactionRepository.findById(testTransaction.getId()).get().getPaid());
    }

    /**
     * Tests if the shipped is saved in the Database of transaction.
     * @throws Exception
     */
    @Test
    public void isShippedSetInDatabase() throws Exception {
        Company testCompanyBuyer = new Company("testCompanyBuyer");
        companyRepository.save(testCompanyBuyer);
        Company testCompanySeller = new Company("testCompanySeller");
        companyRepository.save(testCompanySeller);

        Transaction testTransaction = new Transaction(testCompanyBuyer, testCompanySeller);
        testTransaction.setShipped(true);
        transactionRepository.save(testTransaction);
        assertTrue(transactionRepository.findById(testTransaction.getId()).get().getShipped());
    }

    /**
     * Tests if the actionType is saved in the Repository.
     * @throws Exception
     */
    @Test
    public void isActionTypeSavedInDatabase() throws Exception {
        ActionType testActionType = new ActionType("testActionType", "testDescription", InitiatorType.BUYER);
        actionTypeRepository.save(testActionType);

        assertEquals(testActionType, actionTypeRepository.findById(testActionType.getId()).get());
    }

    /**
     * Tests if the action is saved in the Repository.
     * @throws Exception
     */
    @Test
    public void isActionSavedInDatabase() throws Exception {
        Company testCompanyBuyer = new Company("testCompanyBuyer");
        companyRepository.save(testCompanyBuyer);
        Company testCompanySeller = new Company("testCompanySeller");
        companyRepository.save(testCompanySeller);

        Transaction testTransaction = new Transaction(testCompanyBuyer, testCompanySeller);
        transactionRepository.save(testTransaction);

        ActionType testActionType = new ActionType("testActionType", "testDescription", InitiatorType.BUYER);
        actionTypeRepository.save(testActionType);

        Action actionTest = new Action("testMessage", testActionType, testTransaction);
        actionRepository.save(actionTest);

        assertEquals(actionTest, actionRepository.findById(actionTest.getId()).get());
    }
    //test if company logo is saved in repository
    //test if paid can be set before confirmed




}
