package sopro;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;



import sopro.model.Company;
import sopro.model.User;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;

@SpringBootTest
@Transactional
public class DatabaseTest {


    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    
    @Autowired
    BeforeTest beforeTest;


    /**
     * 
     *
     * @throws Exception
     */
    @BeforeTransaction
    void setup() {
        beforeTest.setup();
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
        assertEquals(testUser, userRepository.findById(testUser.getId()).get()); // .get(): optional<User> in User
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
        assertEquals(testCompany, testUser.getCompany());
    
    }
}
