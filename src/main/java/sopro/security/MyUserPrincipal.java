package sopro.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import sopro.model.User;

/**
 * TODO Doku 
 * Wofür ist das da?
 */
public class MyUserPrincipal implements UserDetails {
    private User user;
    
    public MyUserPrincipal(User user) {
        this.user = user;
    }

    
    /** 
     * @return Collection<? extends GrantedAuthority>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return AuthorityUtils.createAuthorityList(user.getRole());
    }

    
    /** 
     * @return String
     */
    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return user.getPassword();
    }

    
    /** 
     * @return String
     */
    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return user.getEmail();
    }

    
    /** 
     * @return boolean
     */
    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    
    /** 
     * @return boolean
     */
    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    
    /** 
     * @return boolean
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    
    /** 
     * @return boolean
     */
    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }
}
