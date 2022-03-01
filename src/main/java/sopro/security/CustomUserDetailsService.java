package sopro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import sopro.model.User;
import sopro.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) {
       User user = userRepository.findByEmail(email);
       
       if (user == null) {
            throw new UsernameNotFoundException(email);
       }
       return new MyUserPrincipal(user);
    }

}
