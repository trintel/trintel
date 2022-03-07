package sopro.events;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import sopro.model.User;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getAppUrl() {
        return this.appUrl;
    }

    public User getUser() {
        return this.user;
    }

    // standard getters and setters
}