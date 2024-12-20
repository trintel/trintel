package sopro;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Company;
import sopro.model.CompanyLogo;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.model.util.InitiatorType;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyLogoRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;
import sopro.repository.VerificationTokenRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DatabaseService {

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
    CompanyLogoRepository companyLogoRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    public void clearDatabase() {
        actionRepository.deleteAll();
        actionTypeRepository.deleteAll();
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        companyRepository.deleteAll();
        companyLogoRepository.deleteAll();
        verificationTokenRepository.deleteAll();
    }

    public void setup() {

        // Create demo Users
        User admin = new User(true, true, true, true, "admin", "admin", "admin@admin",
                passwordEncoder.encode("password"), null);
        admin.setRole("ADMIN");
        userRepository.save(admin);

        // Create demo Companys
        Company company1 = new Company("[187]Strassenbande");
        Company company2 = new Company("Streber GmbH");
        Company company3 = new Company("7Bags");
        companyRepository.save(company1);
        companyRepository.save(company2);
        companyRepository.save(company3);

        // Create demo Students
        User student1 = new User(true, true, true, true, "Schniedelus", "Maximilius", "m@m",
                passwordEncoder.encode("password"), null);
        student1.setRole("STUDENT");

        User student2 = new User(true, true, true, true, "Speckmann", "Jonas", "j@j",
                passwordEncoder.encode("password"), company1);
        student2.setRole("STUDENT");

        User student3 = new User(true, true, true, true, "Mayo", "Luca", "l@l", passwordEncoder.encode("password"),
                company2);
        student3.setRole("STUDENT");

        User student4 = new User(true, true, true, true, "Vielesorgen", "Felix", "f@f",
                passwordEncoder.encode("password"), company3);
        student4.setRole("STUDENT");

        userRepository.save(student1);
        userRepository.save(student2);
        userRepository.save(student3);
        userRepository.save(student4);

        // Create demo Action_types
        ActionType request = new ActionType("Request", "Demo request text.", InitiatorType.BUYER);
        ActionType offer = new ActionType("Offer", "Demo offer text.", InitiatorType.SELLER);
        ActionType accept = new ActionType("Accept", "Demo offer text.", InitiatorType.SELLER);

        Transaction transaction1 = new Transaction(company1, company2);
        transaction1.setProduct("Product 1");

        Action trans1Request = new Action("Test message request", request, transaction1, null);
        trans1Request.setInitiator(student2);

        Action trans1Offer = new Action("Test message offer", offer, transaction1, null);
        trans1Offer.setAmount(20);
        trans1Offer.setPricePerPiece(0.4);
        trans1Offer.setInitiator(student3);

        Action trans1Accept = new Action("Test message accept", accept, transaction1, null);
        transaction1.setConfirmed(true);
        trans1Accept.setInitiator(student2);

        actionTypeRepository.save(request);
        actionTypeRepository.save(offer);
        actionTypeRepository.save(accept);

        transactionRepository.save(transaction1);

        actionRepository.save(trans1Accept);
        actionRepository.save(trans1Offer);
        actionRepository.save(trans1Request);

        try {
            // byte[] defaultImg =
            // Files.readAllBytes(Paths.get("src/main/ressources/static/img/onlyicon.png").normalize().toAbsolutePath());
            File f = new File("./src/main/resources/static/img/placeholder.jpg");
            BufferedImage image = ImageIO.read(f);
            CompanyLogo companyLogo = new CompanyLogo();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            companyLogo.setLogo(baos.toByteArray());
            companyLogoRepository.save(companyLogo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
