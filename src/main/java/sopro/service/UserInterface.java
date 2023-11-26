package sopro.service;

import javax.servlet.http.HttpServletRequest;

import sopro.model.User;
import sopro.model.VerificationToken;
import sopro.model.util.TokenStatus;

public interface UserInterface {
    void createVerificationTokenForUser(User user, String token);

    void createResetTokenForUser(final User user, final String token);

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    VerificationToken getVerificationToken(String VerificationToken);

    TokenStatus validateVerificationToken(String token, HttpServletRequest request);

    TokenStatus validateResetToken(String token, String password);

    void createUser(User user, String role, HttpServletRequest request) throws IllegalArgumentException;

    void changePassword(User user, String password);

    void changeName(User user, String forename, String surname);

    void requestPasswordReset(String email, HttpServletRequest request);
}
