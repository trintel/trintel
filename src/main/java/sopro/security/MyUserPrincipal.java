package sopro.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import sopro.model.User;

/**
 * A Class for an Authorized User.
 */
public class MyUserPrincipal extends User implements UserDetails {


    public MyUserPrincipal(User user) {
        super(user.getSurname(), user.getForename(), user.getEmail(), user.getPassword());
    }

    /**
     * 
     * @return Collection<? extends GrantedAuthority>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

        //authorities have to begin with "ROLE_", because Spring is stupid
        list.add(new SimpleGrantedAuthority("ROLE_" + super.getRole()));

        return list;
    }

    
    /** 
     * return the email as username, since in our case the email functions as username
     * @return String
     */
    @Override
    public String getUsername() {
        return super.getEmail();
    }

    /**
     * @return boolean
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * @return boolean
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * @return boolean
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @return boolean
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
