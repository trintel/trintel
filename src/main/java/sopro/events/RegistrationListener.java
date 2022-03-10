package sopro.events;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import sopro.model.User;
import sopro.service.UserInterface;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserInterface service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }


    /**
     * Creates token and send email.
     *
     * @param event
     */
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationTokenForUser(user, token);

        final SimpleMailMessage email = constructEmailMessage(event, user, token);
        mailSender.send(email);
    }


    /**
     * Construct and send mail.
     *
     * @param event
     * @param user
     * @param token
     * @return SimpleMailMessage
     */
    private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final User user, final String token) {
        final String recipientAddress = user.getEmail();
        final String subject = messages.getMessage("emailSubject", null, "Registration Confirmation", event.getLocale());
        final String confirmationUrl = "http://" + event.getAppUrl() + "/registrationConfirm?token=" + token;
        final String message = messages.getMessage("emailVerify", null, "You registered successfully. To confirm your registration, please click on the below link.", event.getLocale());
        final String hello = messages.getMessage("emailHello", null, "Hello", event.getLocale());
        final String greetings = messages.getMessage("emailGreetings", null, "Kind regards", event.getLocale());
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(hello + " " + user.getForename() + ",\r\n\r\n" + message + "\r\n\r\n" + confirmationUrl + "\r\n\r\n" + greetings + " - The Trintel Team");
        email.setFrom("trintel@cau.zeppel.eu");
        return email;
    }
}
