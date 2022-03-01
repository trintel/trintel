package sopro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class TrintelApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TrintelApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		// write stuff
	}

}