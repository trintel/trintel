package sopro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import sopro.model.User;
import sopro.repository.UserRepository;

@Transactional
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

        if (user == null)
            throw new UsernameNotFoundException(email);

        return user;
    }
}
