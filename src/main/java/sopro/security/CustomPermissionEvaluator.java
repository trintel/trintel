package sopro.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import sopro.model.Transaction;
import sopro.model.User;
import sopro.repository.TransactionRepository;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permission) {
        if(!(authentication.getPrincipal() instanceof User)) {      //the principal has to be of our custom user type.
            return true;
        }
        User user = (User) authentication.getPrincipal();
        if(user.getRole().equals("ADMIN")) {
            return true;        //admin is always allowed
        }

        if(targetObject instanceof Long && permission.equals("transaction")) {
            List<Transaction> transactions = new ArrayList<>();
            transactions.addAll(transactionRepository.findByBuyer(user.getCompany()));
            transactions.addAll(transactionRepository.findBySeller(user.getCompany()));
            for(Transaction transaction : transactions) {
                if(transaction.getId().equals(targetObject)) {
                    return true;
                }
            }
            // if(transactions.stream().filter(t -> t.getId().equals(targetObject)).count() != 0) {        //only allow, if the users company is involved in the transaction
            //     return true;
            // }
        }

        if(targetObject instanceof Long && permission.equals("company")) {
            return user.getCompany().getId().equals(targetObject);
        }


        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        // TODO Auto-generated method stub
        return true;
    }


}
