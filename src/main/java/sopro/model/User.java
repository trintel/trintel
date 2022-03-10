package sopro.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import sopro.repository.CompanyRepository;

/**
  * Defines the class User with all needed entries for the Database.
  */
@Entity
public class User implements UserDetails {
    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter	@NotEmpty @Column(unique = true) private String email;
    @Getter @Setter @NotEmpty private String surname;
    @Getter @Setter	@NotEmpty private String forename;
    @Getter @Setter	@NotEmpty private String password;
    @Getter @Setter	@NotEmpty private String role;
    @Getter @Setter	@ManyToOne private Company company;

    @Getter @Setter private boolean enabled = false;
    @Getter @Setter private boolean accountNonExpired = true;
    @Getter @Setter private boolean credentialsNonExpired = true;
    @Getter @Setter private boolean accountNonLocked = true;


    public User() {}

    public User(String surname,String forename, String email, String password, Company company) {
       this.surname = surname;
       this.forename = forename;
       this.email = email;
       this.password = password;
       this.company = company;
    }

    public User(boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, String surname,String forename, String email, String password, Company company) {
      this.surname = surname;
      this.forename = forename;
      this.email = email;
      this.password = password;
      this.company = company;
      this.enabled = enabled;
      this.accountNonExpired = accountNonExpired;
      this.credentialsNonExpired = credentialsNonExpired;
      this. accountNonLocked = accountNonLocked;
   }

    /**
     * @return Collection<? extends GrantedAuthority>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

      //authorities have to begin with "ROLE_", because Spring is stupid
      grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.getRole()));   // assuming every user has only one role.

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

        if(this.company != null)
            m.put("company", company.getId());
        else
            m.put("company", null); // ! Careful with null.

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
