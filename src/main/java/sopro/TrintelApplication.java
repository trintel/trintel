package sopro;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import sopro.service.SignupUrlInterface;
import sopro.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class TrintelApplication implements CommandLineRunner {

    @Autowired
    InitDatabaseService initDatabaseService;

    @Autowired
    SignupUrlInterface signupUrlService;

    public static final Logger logger = LoggerFactory.getLogger(TrintelApplication.class);

    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "true");
        SpringApplication.run(TrintelApplication.class, args);
    }

    @Override
    public void run(String... arg0) throws Exception {
        signupUrlService.generateAdminSignupURL();
        signupUrlService.generateStudentSignupURL();
        //initDatabaseService.init();
    }
}
