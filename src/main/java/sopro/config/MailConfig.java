package sopro.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MailConfig {

    @Bean
    @Profile("dev") // This makes sure the dummy is only used in the development profile
    public JavaMailSender javaMailSender() {
        // Using Mockito to mock the JavaMailSender interface
        return Mockito.mock(JavaMailSender.class);
    }
}
