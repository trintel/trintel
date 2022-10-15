package sopro.service;

import sopro.events.OnRegistrationCompleteEvent;
import sopro.model.User;

public interface MailInterface {
    void sendVerificationEmailMessage(final OnRegistrationCompleteEvent event, final User user, final String token);
}
