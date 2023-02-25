package sopro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import sopro.service.InitDatabaseService;
import sopro.service.SignupUrlInterface;

@SpringBootApplication
public class TrintelApplication implements CommandLineRunner {

    @Autowired
    InitDatabaseService initDatabaseService;

    @Autowired
    SignupUrlInterface signupUrlService;

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    public static final Logger logger = LoggerFactory.getLogger(TrintelApplication.class);

    //TODO: We should get rid of all file-system uses.
    public static final String EXPORT_PATH = "."; // Where e.g. SQL dumps are stored during runtime. (only for temporary storrage.)

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "true");
        context = SpringApplication.run(TrintelApplication.class, args);
    }

    @Override
    public void run(String... arg0) throws Exception {
        //signup urls are generated on ervery restart. This is intentional
        signupUrlService.generateAdminSignupURL();
        signupUrlService.generateStudentSignupURL();
        // For tests
        if(activeProfile.equals("dev")) {
            initDatabaseService.init();
        }
        // For final deploy
        initDatabaseService.deployinit();
    }
}
