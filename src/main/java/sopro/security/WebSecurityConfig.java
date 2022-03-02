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
     * Password Encoder
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/**
	 * Hier können Rechte für verschiedene Rollen vergeben werden  
	 * 
	 * @param http
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
		http
            .csrf().disable()
			.authorizeRequests()
				.antMatchers("/", "/console/**", "/signup/**").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				// .loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll();
	}

    
    /** 
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

	
	/**
	 * TODO Doku 
	 * 
	 * @return UserDetailsService
	 */
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		// UserDetails user =
		// 	 User.withDefaultPasswordEncoder()
		// 		.username("user")
		// 		.password("password")
		// 		.roles("USER")
		// 		.build();

		return new CustomUserDetailsService();
	}


}
