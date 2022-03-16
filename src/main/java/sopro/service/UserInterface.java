package sopro.service;

import sopro.model.User;
import sopro.model.VerificationToken;

public interface UserInterface {
    void createVerificationTokenForUser(User user, String token);

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    VerificationToken getVerificationToken(String VerificationToken);

    String validateVerificationToken(String token);

    void createUser(User user, String role) throws Exception;

    void changePassword(User user, String password);

    void changeName(User user, String forename, String surname);
}
