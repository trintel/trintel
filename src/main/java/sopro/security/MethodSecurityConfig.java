package sopro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig  extends GlobalMethodSecurityConfiguration {

    @Autowired
    private CustomPermissionEvaluator customPermissionEvaluator;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler();     //use our owm handler.
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator);                                        //and set our own modified evaluator.
        return expressionHandler;
    }

}
