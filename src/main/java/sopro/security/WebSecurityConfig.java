package sopro.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * add the new UserDetailsService as a Bean, so Spring knows it
     * @return UserDetailsService
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    /**
     * Password Encoder.
     * add Password Encoder as a bean, so that it can be Autowired in other Classes (e.g. UserController)
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure which role can access what pages.
     * Also works with annotations in the controllers. (https://www.baeldung.com/spring-security-method-security)
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http
            .authorizeRequests()
                .antMatchers(
                    "/console/**",
                    "/signup/**",
                    "/webjars/**",
                    "/css/*",
                    "/img/*",
                    "/login/**",
                    "/verify-your-email",
                    "/registrationConfirm/**").permitAll() // permit all to access those Mathes
                .antMatchers("/companies/", "/company/*/edit", "/company/logo/**").hasAnyRole("ADMIN", "STUDENT") // TODO: differentiate access of those two roles
                .antMatchers("/companies/add/**", "/companies/save/**", "/students/**", "/student/{id}/reassign").hasRole("ADMIN")
                .antMatchers("/company/**").hasRole("STUDENT")
                // .antMatchers("/console/**").hasRole("ADMIN") // restrict to only ADMIN role is able to access /console
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login").defaultSuccessUrl("/home", true)
                .permitAll()
                .and()
            .logout()
                .permitAll();
        http.csrf().ignoringAntMatchers("/console/**")
            .and().headers().frameOptions().sameOrigin();
    }

    /**
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //inject our new userDetailsService and add the password encoder
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }


}
