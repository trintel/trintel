package sopro.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopro.model.User;
import sopro.model.VerificationToken;
import sopro.repository.VerificationTokenRepository;

@Service
@Transactional
public class UserService implements UserInterface {
    
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }
}
