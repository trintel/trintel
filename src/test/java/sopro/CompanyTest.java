package sopro;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import sopro.model.Company;
import sopro.model.User;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CompanyTest {

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


    String companyName = "testCompany";

    @Autowired
    BeforeTest beforeTest;

    @BeforeTransaction
    void setup() {
        beforeTest.setup();
    }


    // #######################################################################################
    // ----------------------------------- Method Tests ADMIN
    // #######################################################################################

    /**
     * Tests if the Company page is reachable for the Admin.
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void companiesReachable() throws Exception {
        mockMvc.perform(get("/companies"))
               .andExpect(status().is(200));
    }

    /**
     * Tests if the right view is displayed when the Admin wants to see all
     * Companies.
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void listCompaniesTestAdmin() throws Exception {
        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(view().name("company-list"));
    }

    /**
     * Tests if the students can see the list of all companys for transactions.
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void listCompaniesTestStudent() throws Exception {
        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(view().name("company-list"));
    }

    /**
     * Tests if the Admin can acces the add view.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void addCompanieTestAdmin() throws Exception {
        mockMvc.perform(get("/companies/add"))
                .andExpect(status().isOk());
    }

    /**
     * Tests if the Student can not acces the add view.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = { "STUDENT" })
    public void addCompanieTestStudent() throws Exception {
        mockMvc.perform(get("/companies/add"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests if the Admin can acces the view to save a company before he adds a
     * company.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void saveCompanieTestAdmin() throws Exception {
        // Deletes Company if it is allready in the database from other test
        Company testCompany = new Company("companyName");
        mockMvc.perform(post("/companies/save").flashAttr("company", testCompany).with(csrf()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/companies")); // Test with database check is down below
    }

    /**
     * Tests if the Student can not acces the view to save a company, and save it to
     * the database.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = { "STUDENT" })
    public void saveCompanieTestStudent() throws Exception {
        mockMvc.perform(get("/companies/save"))
               .andExpect(status().isForbidden());
    }

    /**
     * Test before function 
     * Controller has unsaved Company -> fixed in Controller & Frontend
     *
     * Tests if the Admin can acces the list of all students.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void listAllStudentsTestAdmin() throws Exception {
        mockMvc.perform(get("/students"))
               .andExpect(status().isOk())
               .andExpect(view().name("students-list"));
    }

    /**
     * Test before function
     * Tests if the Student can not acces the list of all students.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = { "STUDENT" })
    public void listAllStudentsTestStudent() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests if the student can be reassigned by the admin.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "adminTest@admin", roles = { "ADMIN" })
    public void editReassignStudentTestAdmin() throws Exception {
        // Deletes Company if it is allready in the database from other tests.
        if (companyRepository.findByName(companyName) != null)
            companyRepository.delete(companyRepository.findByName(companyName));
        // Deletes User if it is allready in the database from other tests.
        if (userRepository.findByEmail("adminTest@admin") != null)
            userRepository.delete(userRepository.findByEmail("adminTest@admin"));

        User testUser = new User("adminTest", "adminTest", "adminTest@admin", "password", null);
        testUser.setRole("ADMIN");
        userRepository.save(testUser);
        long id = testUser.getId();
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);

        mockMvc.perform(get("/student/" + id + "/reassign"))
                .andExpect(view().name("student-reassign"));
    }

    /**
     * Tests if the student can be reassigned by himself.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "studentTest@student", roles = { "STUDENT" })
    public void editReassignStudentTestStudent() throws Exception {
        // Deletes Company if it is allready in the database from other tests.
        if (companyRepository.findByName(companyName) != null)
            companyRepository.delete(companyRepository.findByName(companyName));
        // Deletes User if it is allready in the database from other tests.
        if (userRepository.findByEmail("studentTest@student") != null)
            userRepository.delete(userRepository.findByEmail("studentTest@student"));

        User testUser = new User("studentTest", "studentTest", "studentTest@student", "password", null);
        testUser.setRole("STUDENT");
        userRepository.save(testUser);
        long id = testUser.getId();
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);

        mockMvc.perform(get("/student/" + id + "/reassign"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests if the student can be moved to another company by admin.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "adminTest@admin", roles = { "ADMIN" })
    public void moveToCompanyTestAdmin() throws Exception {
        // Deletes Company if it is allready in the database from other tests.
        if (companyRepository.findByName(companyName) != null)
            companyRepository.delete(companyRepository.findByName(companyName));
        // Deletes User if it is allready in the database from other tests.
        if (userRepository.findByEmail("adminTest@admin") != null)
            userRepository.delete(userRepository.findByEmail("adminTest@admin"));

        User testUser = new User("adminTest", "adminTest", "adminTest@admin", "password", null);
        testUser.setRole("ADMIN");
        userRepository.save(testUser);
        long id = testUser.getId();
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);

        mockMvc.perform(post("/student/" + id + "/reassign").param("name", companyName).with(csrf()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/students"));
    }

    /**
     * Tests if the student can be moved to another company by himself.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "studentTest@student", roles = { "STUDENT" })
    public void moveToCompanyTestStudent() throws Exception {
        // Deletes Company if it is allready in the database from other tests.
        if (companyRepository.findByName(companyName) != null)
            companyRepository.delete(companyRepository.findByName(companyName));
        // Deletes User if it is allready in the database from other tests.
        if (userRepository.findByEmail("studentTest@student") != null)
            userRepository.delete(userRepository.findByEmail("studentTest@student"));

        User testUser = new User("studentTest", "studentTest", "studentTest@student", "password", null);
        testUser.setRole("STUDENT");
        userRepository.save(testUser);
        long id = testUser.getId();
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);

        mockMvc.perform(post("/student/" + id + "/reassign").param("name", companyName).with(csrf()))
                .andExpect(status().isForbidden());
    }

    // #######################################################################################
    // ----------------------------------- Method Tests STUDENT
    // #######################################################################################

    /**
     * Test before function
     * Tests if the Admin can not acces the company selection page.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void companySelectTestAdmin() throws Exception {
        mockMvc.perform(get("/company/select"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test before function
     * Tests if the Student can acces the company selection page.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = { "STUDENT" })
    public void companySelectTestStudent() throws Exception {
        mockMvc.perform(get("/company/select"))
                .andExpect(status().isOk())
                .andExpect(view().name("company-select"));
    }

    /**
     * Tests if the Admin can not select a company.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void companySelectIdTestAdmin() throws Exception {
        // Deletes Company if it is allready in the database from other tests
        if (companyRepository.findByName(companyName) != null) {
            companyRepository.delete(companyRepository.findByName(companyName));
        }
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        long id = testCompany.getId();

        mockMvc.perform(get("/company/select/" + id))
                .andExpect(status().isForbidden());

        companyRepository.delete(testCompany);
    }

    /**
     * Tests if the Student can select the company.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "student@student", roles = { "STUDENT" })
    public void companySelectIdTestStudent() throws Exception {
        // Deletes Company if it is allready in the database from other tests.
        if (companyRepository.findByName(companyName) != null) {
            companyRepository.delete(companyRepository.findByName(companyName));
        }
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        long id = testCompany.getId();

        mockMvc.perform(get("/company/select/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("company-view"));

        companyRepository.delete(testCompany);
    }

    /**
     * Tests if the Admin can not join a company.
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void joinCompany2TestAdmin() throws Exception {
        mockMvc.perform(post("/company/join").param("name", companyName).with(csrf()))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests if the Student can join a company.
     *
     * @throws Exception
     */
    @Test //war vorher studentTest@student
    @WithUserDetails(value = "m@m", userDetailsServiceBeanName = "userDetailsService")
    public void joinCompany2TestStudent() throws Exception {
        mockMvc.perform(post("/company/join").param("name", companyRepository.findAll().get(0).getName()).with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/home"));
    }

    /**
     * Tests if the Admin can not edit his own company (has none).
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void editOwnCompanyTestAdmin() throws Exception {
        // Deletes Company if it is allready in the database from other tests
        if (companyRepository.findByName(companyName) != null) {
            companyRepository.delete(companyRepository.findByName(companyName));
        }
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        long companyId = testCompany.getId();

        mockMvc.perform(get("/company/" + companyId + "/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("company-edit"));
    }

    /**
     * Tests if the Student can edit his own company (has one). ... Funktioniert tatsächlich nicht, wenn es ausgeführt wird
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void editOwnCompanyStudentTest() throws Exception {
        mockMvc.perform(get("/company/" + (userRepository.findByEmail("j@j").getCompany()).getId() + "/edit"))
               .andExpect(view().name("company-edit"));
    }


    /**
     * Tests if the Admin can edit an existing company.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "userDetailsService")
    public void saveOwnCompanyTestAdmin() throws Exception {
        if (companyRepository.findByName(companyName) != null) {
            companyRepository.delete(companyRepository.findByName(companyName));
        }
        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        long companyId = testCompany.getId();

        MockMultipartFile file = new MockMultipartFile("formFile", "".getBytes());

        mockMvc.perform(multipart("/company/" + companyId + "/edit").file(file).with(csrf())
               .flashAttr("company", companyId))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/companies/" + companyId));

    }


    /**
     *
     * Tests if the Student can save his company.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value = "j@j", userDetailsServiceBeanName = "userDetailsService")
    public void saveOwnCompanyTestStudent() throws Exception {
        MockMultipartFile file = new MockMultipartFile("formFile", "".getBytes());

        mockMvc.perform(multipart("/company/" + userRepository.findByEmail("j@j").getCompany().getId() + "/edit").file(file).with(csrf())
               .flashAttr("company", userRepository.findByEmail("j@j").getCompany().getId()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/companies/" + userRepository.findByEmail("j@j").getCompany().getId()));

    }

    // #######################################################################################
    // ----------------------------------- Integration Tests
    // #######################################################################################

    /**
     * Tests, if the the admin can add a company.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void adminAddCompany() throws Exception {
        Company testCompany = companyRepository.findByName(companyName);

        if (testCompany != null)
            companyRepository.delete(testCompany);

        mockMvc.perform(get("/companies/add"))
                .andExpect(status().is(200));

        mockMvc.perform(post("/companies/save").param("name", companyName).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/companies"))
                .andExpect(status().isFound());

        assertNotEquals(null, companyRepository.findByName(companyName));
    }

    /**
     * Test written before Function implemented
     * Tests, if the added Company can be deleted.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = { "ADMIN" })
    public void adminDeleteCompany() throws Exception {
        // Deletes Company if it is allready in the database for some reason
        if (companyRepository.findByName(companyName) != null)
            companyRepository.delete(companyRepository.findByName(companyName));

        Company testCompany = new Company(companyName);
        companyRepository.save(testCompany);
        long id = testCompany.getId();

        mockMvc.perform(post("/companies/delete/" + id).param("name", companyName).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/companies"))
                .andExpect(status().isFound());
    }
}
