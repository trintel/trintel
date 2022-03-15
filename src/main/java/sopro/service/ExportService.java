package sopro.service;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Company;
import sopro.TrintelApplication;
import sopro.model.CompanyLogo;
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
public class ExportService implements ExportInterface {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CompanyLogoRepository companyLogoRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ActionTypeRepository actionTypeRepository;

    @Autowired
    ActionRepository actionRepository;

    private static final String EXPORT_PATH = ".";

    /**
     * Exports all data to JSON.
     * 
     * @return Path to json File.
     */
    public String export() {
        JSONObject export = new JSONObject();
        export.put("user", exportUsers());
        export.put("verificationToken", exportVerificationToken());
        export.put("transaction", exportTransaction()); // TODO Felix wegen action list
        export.put("companyLogo", exportCompanyLogo());
        export.put("company", exportCompany()); // TODO same wie oben -> Felix
        export.put("actionType", exportActionType());
        export.put("action", exportAction());

        TrintelApplication.logger.info("Export:\n\n" + export);
        String fileName = "trintel-export-" + new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date()) + ".json";

        try (FileWriter file = new FileWriter(EXPORT_PATH + "/" + fileName)) {
            file.write(export.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EXPORT_PATH +"/"+ fileName ;
    }


    private JSONArray exportAction() {
        JSONArray a = new JSONArray();
        for (Action t : actionRepository.findAll())
            a.add(new JSONObject(t.toMap()));
        return a;
    }
    
    private JSONArray exportActionType() {
        JSONArray a = new JSONArray();
        for (ActionType t : actionTypeRepository.findAll())
            a.add(new JSONObject(t.toMap()));
        return a;
    }

    private JSONArray exportCompany() {
        JSONArray a = new JSONArray();
        for (Company t : companyRepository.findAll())
            a.add(new JSONObject(t.toMap()));
        return a;
    }

    private JSONArray exportCompanyLogo() {
        JSONArray a = new JSONArray();
        for (CompanyLogo t : companyLogoRepository.findAll())
            a.add(new JSONObject(t.toMap()));
        return a;
    }

    private JSONArray exportTransaction() {
        JSONArray a = new JSONArray();
        for (Transaction t : transactionRepository.findAll())
            a.add(new JSONObject(t.toMap()));
        return a;
    }

    private JSONArray exportVerificationToken() {
        JSONArray a = new JSONArray();
        for (VerificationToken vt : verificationTokenRepository.findAll())
            a.add(new JSONObject(vt.toMap()));
        return a;
    }

    private JSONArray exportUsers() {
        JSONArray a = new JSONArray();
        for (User user : userRepository.findAll())
            a.add(new JSONObject(user.toMap()));
        return a;
    }

}
