package sopro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    PasswordEncoder passwordEncoder;

	public void init() {

		// If there is no data, add some initial values for testing the application.
		// ATTENTION: If you change any model (i.e., the data scheme), you most likely
		// need to delete the .h2 database file in your file system first!
		
		if (userRepository.count() == 0 && companyRepository.count() == 0) {

            //Create demo Users
            User admin = new User("admin", "admin", "admin@admin", passwordEncoder.encode("password"), null);
            admin.setRole("ADMIN");
            userRepository.save(admin);
            
            //Create demo Companys
            Company company1 = new Company("[187]Strassenbande");
            Company company2 = new Company("Streber GmbH");
            Company company3 = new Company("7Bags");
            companyRepository.save(company1);
            companyRepository.save(company2);
            companyRepository.save(company3);

            
			// Create demo Students
			User student1 = new User("Schniedelus", "Maximilius", "m@m", passwordEncoder.encode("password"), null);
            student1.setRole("STUDENT");
            User student2 = new User("Speckmann", "Jonas", "j@j", passwordEncoder.encode("password"), company1);
            student2.setRole("STUDENT");
            User student3 = new User("Mayo", "Luca", "l@l", passwordEncoder.encode("password"), company1);
            student3.setRole("STUDENT");
            User student4 = new User("Vielesorgen", "Felix", "f@f", passwordEncoder.encode("password"), company3);
            student4.setRole("STUDENT");
            userRepository.save(student1);
            userRepository.save(student2);
            userRepository.save(student3);
            userRepository.save(student4);


            //Create demo Action_types
            ActionType request = new ActionType("Request", "Demo request text.");
            ActionType offer = new ActionType("Offer", "Demo offer text.");
            ActionType accept = new ActionType("Accept", "Demo offer text.");

            Transaction transaction1 = new Transaction(company1, company2);

            Action trans1Request = new Action("Test message", request, transaction1);
            transaction1.setProduct("Product 1");
            trans1Request.setAmount(10);
            trans1Request.setPricePerPiece(0.5);

            actionTypeRepository.save(request);
            actionTypeRepository.save(offer);
            actionTypeRepository.save(accept);
            transactionRepository.save(transaction1);
            actionRepository.save(trans1Request);

		}
	}

}
