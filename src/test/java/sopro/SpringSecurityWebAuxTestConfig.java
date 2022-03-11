package sopro;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

import sopro.security.CustomUserDetailsService;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
}
