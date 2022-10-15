package sopro.service;

import java.util.Calendar;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sopro.events.OnPasswordResetEvent;
import sopro.model.ResetToken;
import sopro.model.User;
import sopro.model.VerificationToken;
import sopro.model.util.TokenStatus;
import sopro.repository.ResetTokenRepository;
import sopro.repository.UserRepository;
import sopro.repository.VerificationTokenRepository;

@Service
@Transactional
public class UserService implements UserInterface {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired 
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserRepository userRepository;

    //TODO enum?!
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
        verificationTokenRepository.save(myToken);
    }

    /**
     * Create a token for a user.
     *
     * @param user
     * @param token
     */
    @Override
    public void createResetTokenForUser(final User user, final String token) {
        final ResetToken myToken = new ResetToken(token, user);
        resetTokenRepository.save(myToken);
    }

    /**
     * Get user by verification token.
     *
     * @param verificationToken
     * @return User
     */
    @Override
    public User getUser(String verificationToken) {
        final VerificationToken token = verificationTokenRepository.findByToken(verificationToken);
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
        return verificationTokenRepository.findByToken(VerificationToken);
    }

    /**
     * Check if a token is valid. Delete if not.
     * Also delete old tokens.
     *
     * @param token
     * @return String
     */
    @Override
    public TokenStatus validateVerificationToken(String token) {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TokenStatus.INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return TokenStatus.EXPIRED;
        }

        user.setEnabled(true);
        verificationTokenRepository.delete(verificationToken); // Should not be needed anymore.
        userRepository.save(user);
        return TokenStatus.VALID;
    }

    /**
     * Check if a token is valid. Delete if not.
     * Also delete old tokens.
     *
     * @param token
     * @return String
     */
    @Override
    public TokenStatus validateResetToken(String token, String password) {
        final ResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null) {
            return TokenStatus.INVALID;
        }

        final User user = resetToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((resetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            resetTokenRepository.delete(resetToken);
            return TokenStatus.EXPIRED;
        }

        //TODO: Das hier würde ich gerne auslagern
        user.setPassword(passwordEncoder.encode(password));
        resetTokenRepository.delete(resetToken); // Should not be needed anymore.
        userRepository.save(user);
        return TokenStatus.VALID;
    }

    @Override
    public void createUser(User user, String role) throws Exception {
        user.setRole(role.toUpperCase());

        // encode the new password for saving in the database
        String encPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        // saves the new user in userRepo
        userRepository.save(user);
    }

    @Override
    public void changePassword(User user, String password) {
        User databaseUser = userRepository.findByEmail(user.getEmail());        //to be sure about the id
        databaseUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(databaseUser);
    }

    @Override
    public void changeName(User user, String forename, String surname) {
        User databaseUser = userRepository.findByEmail(user.getEmail());        //to be sure about the id
        databaseUser.setForename(forename);
        databaseUser.setSurname(surname);
        userRepository.save(databaseUser);
    }

    @Override
    public void requestPasswordReset(String email, HttpServletRequest request) {
        if(!userRepository.existsByEmail(email)) {
            return; //user could not be found
        }
        User user = userRepository.findByEmail(email);
        String token = UUID.randomUUID().toString();
        createResetTokenForUser(user, token);

        // Publish event for reset Mail.
        eventPublisher.publishEvent(new OnPasswordResetEvent(user, request.getLocale(),
                request.getServerName() + ":" + request.getServerPort()));
    }
}
