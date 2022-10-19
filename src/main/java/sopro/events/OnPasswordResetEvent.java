package sopro.events;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import sopro.model.User;

import lombok.Getter;

@Getter
public class OnPasswordResetEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnPasswordResetEvent(User user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
