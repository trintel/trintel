package sopro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sopro.model.Company;
import sopro.model.User;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;

@Service
public class InitDatabaseService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CompanyRepository companyRepository;

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


		}
	}

}
