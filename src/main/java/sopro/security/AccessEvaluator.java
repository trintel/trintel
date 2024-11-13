package sopro.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import sopro.model.User;

@Component
public class AccessEvaluator {

    /**
     * check if the user is assigned to a company.
     * 
     * @return
     */
    public boolean hasCompany() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((User) auth.getPrincipal());
        return user.getCompany() != null;
    }

    /**
     * test if the user is in the company.
     * 
     * @param companyID
     * @return
     */
    public boolean isInCompany(Long companyID) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((User) auth.getPrincipal());
        if (user.getCompany() == null) {
            return false;
        }
        return user.getCompany().getId().equals(companyID);
    }
}
