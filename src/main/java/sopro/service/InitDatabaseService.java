package sopro.service;

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
import sopro.repository.NonArchivedTransactionRepository;
import sopro.repository.ResetTokenRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;
import sopro.repository.VerificationTokenRepository;

@Service
public class InitDatabaseService {

        @Autowired
        UserRepository userRepository;

        @Autowired
        CompanyRepository companyRepository;

        @Autowired
        ActionTypeRepository actionTypeRepository;

        @Autowired
        ActionRepository actionRepository;

        @Autowired
        TransactionRepository transactionRepository;

        @Autowired
        CompanyLogoRepository companyLogoRepository;

        @Autowired
        ResetTokenRepository resetTokenRepository;

        @Autowired
        VerificationTokenRepository verificationTokenRepository;

        @Autowired
        NonArchivedTransactionRepository nonArchivedTransactionRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        public void init() {
                // If there is no data, add some initial values for testing the application.
                // ATTENTION: If you change any model (i.e., the data scheme), you most likely
                // need to delete the .h2 database file in your file system first!

                if (userRepository.count() == 0 && companyRepository.count() == 0) {

                        // Create demo Users
                        User admin = new User(true, true, true, true, "admin", "admin", "admin@admin",
                                        passwordEncoder.encode("password"), null);
                        admin.setRole("ADMIN");
                        userRepository.save(admin);

                        // Create demo Companies
                        Company company1 = new Company("[187]Strassenbande");
                        Company company2 = new Company("Streber GmbH");
                        Company company3 = new Company("7Bags");
                        company1.setDescription("Cooles 187 Unternehmen");
                        company2.setDescription("Wir sind die coolsten wie wir cruisen");
                        company3.setDescription("Wir verkaufen Rucksäcke");
                        companyRepository.save(company1);
                        companyRepository.save(company2);
                        companyRepository.save(company3);

                        // Create demo Students
                        User student1 = new User(true, true, true, true, "Windlelus", "Maximilius", "m@m",
                                        passwordEncoder.encode("password"), company2);
                        student1.setRole("STUDENT");
                        User student2 = new User(true, true, true, true, "Speckmann", "Jonas", "j@j",
                                        passwordEncoder.encode("password"), company1);
                        student2.setRole("STUDENT");
                        User student3 = new User(true, true, true, true, "Mayo", "Luca", "l@l",
                                        passwordEncoder.encode("password"),
                                        null);
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
                        request.setIconClass("bi bi-pencil");
                        ActionType offer = new ActionType("Offer", "Demo offer text.", InitiatorType.BOTH);
                        offer.setIconClass("bi bi-cart-plus");
                        ActionType accept = new ActionType("Accept", "Demo accept text.", InitiatorType.BOTH);
                        accept.setIconClass("bi bi-check2-circle");
                        accept.setColorClass("success");
                        ActionType cancelBuyer = new ActionType("Cancel", "Option to cancel transaction for buyer.",
                                        InitiatorType.BOTH);
                        cancelBuyer.setIconClass("bi bi-x-circle");
                        cancelBuyer.setColorClass("danger");
                        ActionType delivery = new ActionType("Delivery",
                                        "Action to kick off delivery of goods to buyer.",
                                        InitiatorType.SELLER);
                        delivery.setIconClass("bi bi-truck");
                        ActionType invoicing = new ActionType("Invoicing", "Action to send receipt to buyer.",
                                        InitiatorType.SELLER);
                        invoicing.setIconClass("bi bi-receipt-cutoff");
                        ActionType paid = new ActionType("Paid", "Action to mark transaction as completed.",
                                        InitiatorType.SELLER);
                        paid.setIconClass("bi bi-cash-coin");

                        Transaction transaction1 = new Transaction(company1, company2);
                        transaction1.setProduct("Product 1");

                        Action trans1Request = new Action("Test message", request, transaction1, null);
                        trans1Request.setInitiator(student2);

                        Action trans1Offer = new Action("Test message", offer, transaction1, null);
                        trans1Offer.setAmount(20);
                        trans1Offer.setPricePerPiece(0.4);
                        trans1Offer.setInitiator(student1);

                        Action trans1Accept = new Action("Test message", accept, transaction1, null);
                        transaction1.setConfirmed(true);
                        trans1Accept.setInitiator(student2);

                        actionTypeRepository.save(request);
                        actionTypeRepository.save(offer);
                        actionTypeRepository.save(accept);
                        actionTypeRepository.save(cancelBuyer);
                        actionTypeRepository.save(delivery);
                        actionTypeRepository.save(invoicing);
                        actionTypeRepository.save(paid);

                        transactionRepository.create(transaction1);
                        actionRepository.save(trans1Request);
                        actionRepository.save(trans1Offer);
                        actionRepository.save(trans1Accept);

                        Transaction transaction2 = new Transaction(company3, company1);
                        transaction2.setProduct("Product 2");

                        Action trans2Request = new Action("Test message", request, transaction2, null);
                        trans2Request.setInitiator(student4);

                        transactionRepository.create(transaction2);
                        actionRepository.save(trans2Request);

                        // save the default companylogo in database
                        // ClassLoader classLoader = getClass().getClassLoader();
                        // URL resource =
                        // classLoader.getResource("src/main/ressources/static/img/onlyicon.png");
                        // File img = new Imga(resource.toURI());

                        try {
                                // Correct path for local development
                                File f = new File("./src/main/resources/static/img/placeholder.jpg");
                                if (!f.exists()) {
                                        // Fallback for deployment environment
                                        f = new File("/app/build/resources/main/static/img/placeholder.jpg");
                                }
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

        public void deployinit() {

                if (userRepository.count() == 0 && companyRepository.count() == 0) {

                        // Create demo Users
                        // User admin = new User(true, true, true, true, "admin", "admin",
                        // "admin@admin", passwordEncoder.encode("password"), null);
                        // admin.setRole("ADMIN");
                        // userRepository.save(admin);

                        // Create demo Action_types
                        ActionType request = new ActionType("Request",
                                        "Request a product and its amount from a seller.",
                                        InitiatorType.BUYER);
                        request.setIconClass("bi bi-pencil");
                        ActionType offer = new ActionType("Offer", "Make an initial offer or a counter offer.",
                                        InitiatorType.BOTH);
                        offer.setIconClass("bi bi-cart-plus");

                        ActionType accept = new ActionType("Accept", "Accept the most recent offer.",
                                        InitiatorType.BOTH);
                        accept.setIconClass("bi bi-check2-circle");
                        accept.setColorClass("success");

                        ActionType cancelBuyer = new ActionType("Cancel", "Option to cancel transaction for buyer.",
                                        InitiatorType.BOTH);
                        cancelBuyer.setIconClass("bi bi-x-circle");
                        cancelBuyer.setColorClass("danger");

                        ActionType delivery = new ActionType("Delivery",
                                        "Action to kick off delivery of goods to buyer.",
                                        InitiatorType.SELLER);
                        delivery.setIconClass("bi bi-truck");

                        ActionType invoicing = new ActionType("Invoicing", "Action to send receipt to buyer.",
                                        InitiatorType.SELLER);
                        invoicing.setIconClass("bi bi-receipt-cutoff");

                        ActionType paid = new ActionType("Paid", "Action to mark transaction as completed.",
                                        InitiatorType.SELLER);
                        paid.setIconClass("bi bi-cash-coin");

                        actionTypeRepository.save(request);
                        actionTypeRepository.save(offer);
                        actionTypeRepository.save(accept);
                        actionTypeRepository.save(cancelBuyer);
                        actionTypeRepository.save(delivery);
                        actionTypeRepository.save(invoicing);
                        actionTypeRepository.save(paid);

                        // save the default companylogo in database
                        // ClassLoader classLoader = getClass().getClassLoader();
                        // URL resource =
                        // classLoader.getResource("src/main/ressources/static/img/onlyicon.png");
                        // File img = new Imga(resource.toURI());

                        try {
                                // byte[] defaultImg =
                                // Files.readAllBytes(Paths.get("src/main/ressources/static/img/onlyicon.png").normalize().toAbsolutePath());
                                File f = new File("/app/build/resources/main/static/img/placeholder.jpg");
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

        /**
         * function to reset the database.
         * WARN: all data will be lost.
         */
        public void resetWithAdmin(User admin) {
                // TODO: rework this.
                // delete all records.
                nonArchivedTransactionRepository.deleteAll();
                companyRepository.deleteAll();
                actionTypeRepository.deleteAll();
                userRepository.deleteAll();
                resetTokenRepository.deleteAll();
                verificationTokenRepository.deleteAll();
                // execute deploy init.
                this.deployinit();
                // add the current admin
                userRepository.save(admin);
        }
}
