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
import sopro.model.InitiatorType;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.model.VerificationToken;
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
            importUser((JSONArray) o.get("user"));
            importCompanyLogo((JSONArray) o.get("companyLogo"));
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
            actionRepository.save(new Action(
                    (Long) o.get("id"),
                    actionTypeRepository.findById((Long) o.get("actionType")).get(),
                    transactionRepository.findById((Long) o.get("transaction")).get(),
                    userRepository.findById((Long) o.get("initiator")).get(),
                    (String) o.get("message"),
                    (Integer) o.get("amount"),
                    (Double) o.get("pricePerPiece"),
                    (LocalDate) o.get("date"),
                    (LocalTime) o.get("time")));
        }
    }

    private void importActionType(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            actionTypeRepository.save(new ActionType(
                    (Long) o.get("id"),
                    (String) o.get("name"),
                    (InitiatorType) o.get("initiatorType"), // TODO geht das?
                    (String) o.get("text"),
                    (boolean) o.get("standardAction")));
        }
    }

    private void importTransaction(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            transactionRepository.save(new Transaction(
                    (Long) o.get("id"),
                    companyRepository.findById((long) o.get("buyer")).get(),
                    companyRepository.findById((long) o.get("seller")).get(),
                    (String) o.get("product"),
                    (boolean) o.get("paid"),
                    (boolean) o.get("shipped"),
                    (boolean) o.get("confirmed"),
                    (boolean) o.get("active")));
        }
    }

    private void importVerificationToken(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            verificationTokenRepository.save(new VerificationToken(
                    (Long) o.get("id"),
                    (String) o.get("token"),
                    userRepository.findById((Long) o.get("user")).get(),
                    (Date) o.get("expiryDate")));
        }
    }

    private void importCompanyLogo(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            companyLogoRepository.save(new CompanyLogo(
                    (Long) o.get("id"),
                    Base64.decodeBase64((String) o.get("logo")),
                    companyRepository.findById((long) o.get("company")).get()));
        }
    }

    private void importCompany(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            companyRepository.save(new Company(
                    (Long) o.get("id"),
                    (String) o.get("name"),
                    (String) o.get("description")));
        }
    }

    private void importUser(JSONArray arr) {
        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;
            userRepository.save(new User(
                    (Long) o.get("id"),
                    (String) o.get("email"),
                    (String) o.get("surname"),
                    (String) o.get("forename"),
                    (String) o.get("password"),
                    (String) o.get("role"),
                    companyRepository.findById((long) o.get("company")).get(),
                    (boolean) o.get("enabled"),
                    (boolean) o.get("accountNonExpired"),
                    (boolean) o.get("credentialsNonExpired"),
                    (boolean) o.get("accountNonLocked")));
        }
    }
}
