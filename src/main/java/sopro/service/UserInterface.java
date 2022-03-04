package sopro.service;

import sopro.model.User;

public interface UserInterface {
    void createVerificationTokenForUser(User user, String token);
}
