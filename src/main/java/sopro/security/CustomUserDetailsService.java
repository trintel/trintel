package sopro.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sopro.model.User;
import sopro.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Read username from database.
     * In our case email equals username.
     * 
     * @param email
     * @return a new Authenticated User as "MyPricipalUser"
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(email);
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        //authorities have to begin with "ROLE_", because Spring is stupid
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }

}
