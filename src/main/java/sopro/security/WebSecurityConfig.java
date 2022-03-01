package sopro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import sopro.model.User;
import sopro.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

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
