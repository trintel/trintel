package sopro.service;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.validation.metadata.ReturnValueDescriptor;

import com.fasterxml.jackson.databind.util.JSONPObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import sopro.TrintelApplication;
import sopro.model.User;
import sopro.model.VerificationToken;
import sopro.repository.UserRepository;
import sopro.repository.VerificationTokenRepository;

@Service
public class BackupService {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    UserRepository userRepository;

    public void export() {
        JSONObject export = new JSONObject();
        export.put("userTable", exportUsers());
        export.put("verificationTokenTable", exportVerificationToken());

        TrintelApplication.logger.info("Export:\n\n" + export);
        try (FileWriter file = new FileWriter(
                "trintel-export-" + new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date()) + ".json")) {
            file.write(export.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
