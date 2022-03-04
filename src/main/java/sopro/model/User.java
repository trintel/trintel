package sopro.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    public User(){}

    public User(String surname,String forename, String email, String password, Company company){
       this.surname = surname;
       this.forename = forename;
       this.email = email;
       this.password = password;
       this.company = company;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

      //authorities have to begin with "ROLE_", because Spring is stupid
      grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.getRole()));   //assuming every user has only one role

      return grantedAuthorities;
    }

    @Override
    public String getUsername() {
      // TODO Auto-generated method stub
      return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
      // TODO Auto-generated method stub
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      // TODO Auto-generated method stub
      return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      // TODO Auto-generated method stub
      return true;
    }

    @Override
    public boolean isEnabled() {
      // TODO Auto-generated method stub
      return true;
    }

}