package sopro.service;

import java.util.Calendar;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopro.model.User;
import sopro.model.VerificationToken;
import sopro.repository.UserRepository;
import sopro.repository.VerificationTokenRepository;

@Service
@Transactional
public class UserService implements UserInterface {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    // TODO move registerNewUser here!

    /**
     * Create a token for a user.
     *
     * @param user
     * @param token
     */
    @Override
    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    /**
     * Get user by verification token.
     *
     * @param verificationToken
     * @return User
     */
    @Override
    public User getUser(String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    /**
     * Save user.
     *
     * @param user
     */
    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    /**
     * Get token by value.
     *
     * @param VerificationToken
     * @return VerificationToken
     */
    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    /**
     * Check if a token is valid. Delete if not.
     * Also delete old tokens.
     *
     * @param token
     * @return String
     */
    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        tokenRepository.delete(verificationToken); // Should not be needed anymore.
        userRepository.save(user);
        return TOKEN_VALID;
    }
}