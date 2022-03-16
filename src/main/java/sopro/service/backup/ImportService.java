package sopro.service.backup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopro.TrintelApplication;
import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Company;
import sopro.model.CompanyLogo;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.model.VerificationToken;
import sopro.model.util.InitiatorType;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyLogoRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;
import sopro.repository.VerificationTokenRepository;

@Service
public class ImportService implements ImportInterface {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyLogoRepository companyLogoRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ActionTypeRepository actionTypeRepository;

    @Autowired
    private ActionRepository actionRepository;

    /**
     * Import JSON data.
     *
     * @return returns true if successful.
     */
    @Override
    public boolean importJSON(String path) {
        JSONParser parser = new JSONParser();

        try {
            JSONObject o = (JSONObject) parser.parse(new FileReader(path));

            importCompany((JSONArray) o.get("company"));
            importCompanyLogo((JSONArray) o.get("companyLogo"));
            importUser((JSONArray) o.get("user"));
            importVerificationToken((JSONArray) o.get("verificationToken"));
            importTransaction((JSONArray) o.get("transaction"));
            importActionType((JSONArray) o.get("actionType"));
            importAction((JSONArray) o.get("action"));

        } catch (FileNotFoundException e) {
            TrintelApplication.logger.error("JSON Import: File not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            TrintelApplication.logger.error("JSON Import: Parse error. JSON File might be damaged.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void importAction(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            System.out.println(obj);
            Action a = new Action();

            a.setId((Long) o.get("id"));
            a.setMessage((String) o.get("message"));
            a.setDate((LocalDate) LocalDate.parse((String)o.get("date")));
            a.setTime((LocalTime) LocalTime.parse((String)o.get("time")));

            if(o.get("amount") != null)
                a.setAmount((Integer) ((Long)o.get("amount")).intValue());

            if(o.get("pricePerPiece") != null)
                a.setPricePerPiece((Double) o.get("pricePerPiece"));

            a.setActiontype(actionTypeRepository.findById((Long) o.get("actionType")).get());
            a.setTransaction(transactionRepository.findById((Long) o.get("transaction")).get());
            a.setInitiator(userRepository.findById((Long) o.get("initiator")).get());

            actionRepository.save(a);
        }
    }

    private void importActionType(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            System.out.println(obj);

            ActionType at = new ActionType();

            at.setId((Long) o.get("id"));
            at.setName((String) o.get("name"));
            at.setInitiatorType(InitiatorType.valueOf((String)o.get("initiatorType")));
            at.setText((String) o.get("text"));
            at.setStandardAction((boolean) o.get("standardAction"));

            actionTypeRepository.save(at);
        }
    }

    private void importTransaction(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            System.out.println(obj);

            Transaction t = new Transaction();

            t.setId((Long) o.get("id"));
            t.setPaid((boolean) o.get("paid"));
            t.setShipped((boolean) o.get("shipped"));
            t.setConfirmed((boolean) o.get("confirmed"));
            t.setActive((boolean) o.get("active"));
            t.setProduct((String) o.get("product"));

            // Probably need Exception Handling here. Not sure yet.
            t.setBuyer(companyRepository.findById((long) o.get("buyer")).get());
            t.setSeller(companyRepository.findById((long) o.get("seller")).get());

            transactionRepository.save(t);
        }
    }

    private void importVerificationToken(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            System.out.println(obj);

            VerificationToken vt = new VerificationToken();

            vt.setId((Long) o.get("id"));
            vt.setToken((String) o.get("token"));
            vt.setExpiryDate((Date) o.get("expiryDate"));

            try {
                vt.setUser(userRepository.findById((Long) o.get("user")).get());
            } catch (Exception e) {
                TrintelApplication.logger.info("VerificationToken Import: User Skipped. This is BAD!");
            }

            verificationTokenRepository.save(vt);
        }
    }

    private void importCompanyLogo(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            System.out.println(obj);

            CompanyLogo cl = new CompanyLogo();

            cl.setId((Long) o.get("id"));
            cl.setLogo(Base64.decodeBase64((String) o.get("logo")));

            try {
                cl.setCompany(companyRepository.findById((long) o.get("company")).get());
            } catch (Exception e) {
                TrintelApplication.logger.info("CompanyLogo Import: Company skipped.");
            }

            companyLogoRepository.save(cl);
        }
    }

    private void importCompany(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            System.out.println(obj);
            Company c = new Company();

            c.setId((Long) o.get("id"));
            c.setName((String) o.get("name"));
            c.setDescription((String) o.get("description"));

            companyRepository.save(c);
        }
    }

    private void importUser(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            System.out.println(obj);
            User u = new User();

            u.setId((Long) o.get("id"));
            u.setEmail((String) o.get("email"));
            u.setSurname((String) o.get("surname"));
            u.setForename((String) o.get("forename"));
            u.setPassword((String) o.get("password"));
            u.setRole((String) o.get("role"));
            u.setEnabled((boolean) o.get("enabled"));
            u.setAccountNonExpired((boolean) o.get("accountNonExpired"));
            u.setAccountNonExpired((boolean) o.get("credentialsNonExpired"));
            u.setAccountNonLocked((boolean) o.get("accountNonLocked"));

            try {
                u.setCompany(companyRepository.findById((Long) o.get("company")).get());
            } catch (Exception e) {
                TrintelApplication.logger.info("UserImport: Company skipped.");
            }

            userRepository.save(u);
        }
    }
}
