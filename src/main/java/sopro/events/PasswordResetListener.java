package sopro.events;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import sopro.model.User;
import sopro.service.MailService;
import sopro.service.UserInterface;

@Component
public class PasswordResetListener implements
        ApplicationListener<OnPasswordResetEvent> {

    @Autowired
    private UserInterface userService;

    @Autowired
    private MailService mailService;

    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        createResetToken(event);
    }


    /**
     * Creates token and send email.
     *
     * @param event
     */
    private void createResetToken(OnPasswordResetEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createResetTokenForUser(user, token);

        mailService.sendResetEmailMessage(event, user, token);
    }

}
