package sopro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.Charset;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;


@SpringBootApplication
public class TrintelApplication implements CommandLineRunner {
	
	public static String ADMIN_LOGIN_URL;
	public static String STUDENT_LOGIN_URL;

	private static final Logger logger = LoggerFactory.getLogger(TrintelApplication.class);

	@Autowired
	InitDatabaseService initDatabaseService;


	public static void main(String[] args) {

		String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890";
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		Random random = new Random();
		for(int i = 0; i < 25; i++) {

			// generate random index number
			int index1 = random.nextInt(alphabet.length());
			int index2 = random.nextInt(alphabet.length());
	  
			// get character specified by index
			// from the string
			char randomChar1 = alphabet.charAt(index1);
			char randomChar2 = alphabet.charAt(index2);
	  
			// append the character to string builder
			sb1.append(randomChar1);
			sb2.append(randomChar2);
		  }
	  
		  ADMIN_LOGIN_URL = sb1.toString();
		  STUDENT_LOGIN_URL = sb2.toString();

		// System.setProperty("spring.devtools.restart.enabled", "true");
		SpringApplication.run(TrintelApplication.class, args);
		logger.info("The registration URL for ADMINS is: 		/signup/" + ADMIN_LOGIN_URL);
		logger.info("The registration URL for STUDENTS is: 		/signup/" + STUDENT_LOGIN_URL);

	}

	@Override
	public void run(String... arg0) throws Exception {
		initDatabaseService.init();
	}

}