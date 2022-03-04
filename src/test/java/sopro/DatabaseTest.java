package sopro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import sopro.model.Company;
import sopro.model.User;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DatabaseTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    private User user;

    private Company company;

    String forename = "forename";
    String lastname = "lastname";
    String email = "email@email.com";
    String password = "password";

    /**
     * Creates a User and a Company in the database.
     * 
     * @throws Exception
     */
    @BeforeEach
    public void addUserandCompany() {
        user = new User(lastname, forename, email, password, null);
        user.setRole("STUDENT"); // model -> user: Role not null
        company = new Company("company");
        companyRepository.save(company);
        user.setCompany(company);
        userRepository.save(user);

    }

    /**
     * Checks if a created user is really saved in database.
     * 
     * @throws Exception
     */
    @Test
    public void isUserInDatabase() throws Exception {
        assertEquals(user, userRepository.findById(user.getId()).get()); // .get(): optional<User> in User
    }
}
