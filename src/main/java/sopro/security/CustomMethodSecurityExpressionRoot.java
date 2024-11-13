package sopro.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import sopro.model.User;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     * test if the user is in the company.
     * 
     * @param companyID
     * @return
     */
    public boolean isInCompany(Long companyID) {
        User user = ((User) this.getPrincipal());
        if (user.getCompany() == null) {
            return false;
        }
        return user.getCompany().getId().equals(companyID);
    }

    /**
     * check if the user is assigned to a company.
     * 
     * @return
     */
    public boolean hasCompany() {
        User user = ((User) this.getPrincipal());
        return user.getCompany() != null;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getFilterObject() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getReturnObject() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getThis() {
        // TODO Auto-generated method stub
        return null;
    }

}
