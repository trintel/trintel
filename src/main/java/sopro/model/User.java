package sopro.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import sopro.model.util.IdHandler;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines the class User with all needed entries for the Database.
 */
@Entity
public class User implements UserDetails {

    @Getter @Setter @Id private Long id;
    @Getter @Setter	@NotEmpty @Column(unique = true) private String email;
    @Getter @Setter @NotEmpty private String surname;
    @Getter @Setter	@NotEmpty private String forename;
    @Getter @Setter	@NotEmpty private String password;
    @Getter @Setter	@NotEmpty private String role;
    @Getter @Setter private boolean enabled = false;
    @Getter @Setter private boolean accountNonExpired = true;
    @Getter @Setter private boolean credentialsNonExpired = true;
    @Getter @Setter private boolean accountNonLocked = true;
    @Getter @Setter	@ManyToOne private Company company;

    public User() {
        this.id = IdHandler.generateId();
    }

    public User(String surname, String forename, String email, String password, Company company) {
        this.id = IdHandler.generateId();
        this.surname = surname;
        this.forename = forename;
        this.email = email;
        this.password = password;
        this.company = company;
    }

    public User(boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
            String surname, String forename, String email, String password, Company company) {
        this.id = IdHandler.generateId();
        this.surname = surname;
        this.forename = forename;
        this.email = email;
        this.password = password;
        this.company = company;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * @return Collection<? extends GrantedAuthority>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        // authorities have to begin with "ROLE_", because Spring is stupid
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.getRole())); // assuming every user has only
                                                                                      // one role.

        return grantedAuthorities;
    }

    // Spring wants a username. We use email as username.
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("accountNonExpired", accountNonExpired);
        m.put("accountNonLocked", accountNonLocked);

        if (this.company != null)
            m.put("company", company.getId());
        else
            m.put("company", -1);

        m.put("credentialsNonExpired", credentialsNonExpired);
        m.put("email", email);
        m.put("enabled", enabled);
        m.put("forename", forename);
        m.put("id", id);
        m.put("password", password);
        m.put("role", role);
        m.put("surname", surname);
        return m;
    }
}
