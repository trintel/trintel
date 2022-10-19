package sopro.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import sopro.model.util.IdHandler;

import lombok.Getter;
import lombok.Setter;

@Entity
public class ResetToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @Getter
    @Setter
    private Long id;

    private String token;

    @OneToOne
    private User user;

    private Date expiryDate;

    public ResetToken() {
        this.id = IdHandler.generateId();
    }

    public ResetToken(final String token) {
        this.id = IdHandler.generateId();
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public ResetToken(final String token, final User user) {
        this.id = IdHandler.generateId();
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", this.id);
        m.put("token", this.token);
        m.put("user", this.user.getId());
        m.put("expiryDate", this.expiryDate);
        return m;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Token [String=").append(token).append("]").append("[Expires").append(expiryDate).append("]");
        return builder.toString();
    }
}
