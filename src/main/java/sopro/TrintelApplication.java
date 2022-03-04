package sopro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class TrintelApplication implements CommandLineRunner {

	@Autowired
	InitDatabaseService initDatabaseService;

	public static void main(String[] args) {
		// System.setProperty("spring.devtools.restart.enabled", "true");
		SpringApplication.run(TrintelApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		initDatabaseService.init();
	}

}