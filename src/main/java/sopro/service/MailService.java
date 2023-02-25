package sopro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import sopro.events.OnPasswordResetEvent;
import sopro.events.OnRegistrationCompleteEvent;
import sopro.model.User;

@Service
public class MailService implements MailInterface {

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:trintel@cau.zeppel.eu}")
    String mailUsername;

    /**
     * Construct and send mail.
     *
     * @param event
     * @param user
     * @param token
     * @return SimpleMailMessage
     */
    public void sendVerificationEmailMessage(final OnRegistrationCompleteEvent event, final User user, final String token) {
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
        email.setFrom(mailUsername);

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
    public void sendResetEmailMessage(final OnPasswordResetEvent event, final User user, final String token) {
        final String recipientAddress = user.getEmail();
        final String subject = messages.getMessage("emailSubjectPWReset", null, "Password Reset", event.getLocale());
        final String confirmationUrl = "http://" + event.getAppUrl() + "/reset-password/new?token=" + token;
        final String message = messages.getMessage("emailPWReset", null, "A password reset for your account has been requested. To change your password please click the link below.", event.getLocale());
        final String hello = messages.getMessage("emailHello", null, "Hello", event.getLocale());
        final String greetings = messages.getMessage("emailGreetings", null, "Kind regards", event.getLocale());
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(hello + " " + user.getForename() + ",\r\n\r\n" + message + "\r\n\r\n" + confirmationUrl + "\r\n\r\n" + greetings + " - The Trintel Team");
        email.setFrom(mailUsername);

        mailSender.send(email);
    }
}
