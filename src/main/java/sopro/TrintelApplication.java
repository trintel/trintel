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

    @Value("${spring.profiles.active:Prod}")
    private String activeProfile;

    public static final Logger logger = LoggerFactory.getLogger(TrintelApplication.class);

    public static final String WORKDIR = System.getProperty("user.dir"); // TODO: this links to the last use of the
                                                                         // filesystem. (PdfService)
    public static final String EXPORT_PATH = "."; // Where e.g. SQL dumps are stored during runtime.

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "true");
        context = SpringApplication.run(TrintelApplication.class, args);
    }

    @Override
    public void run(String... arg0) throws Exception {
        signupUrlService.generateAdminSignupURL();
        signupUrlService.generateStudentSignupURL();
        // For tests
        if(activeProfile.equals("debug")) {
            initDatabaseService.init();
        }
        // For final deploy
        initDatabaseService.deployinit();
    }
}
