package sopro.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import sopro.TrintelApplication;

@Service
@Transactional
public class SignupUrlService implements SignupUrlInterface {

    public static String ADMIN_SIGNUP_URL;
    public static String STUDENT_SIGNUP_URL;

    /**
     * Generates a new student signup URL.
     *
     * @return String URL
     */
    @Override
    public String generateStudentSignupURL() {
        STUDENT_SIGNUP_URL = UUID.randomUUID().toString();
        TrintelApplication.logger.info("The registration URL for STUDENTS is: /signup/" + STUDENT_SIGNUP_URL);
        return STUDENT_SIGNUP_URL;
    }

    /**
     * Generate a new admin signup URL.
     *
     * @return String URL
     */
    @Override
    public String generateAdminSignupURL() {
        ADMIN_SIGNUP_URL = UUID.randomUUID().toString();
        TrintelApplication.logger.info("The registration URL for ADMINS is: /signup/" + ADMIN_SIGNUP_URL);
        return ADMIN_SIGNUP_URL;
    }

    /**
     * Get the currently active student signup URL.
     *
     * @return String URL
     */
    @Override
    public String getStudentSignupUrl() {
       return STUDENT_SIGNUP_URL;
    }

    /**
     * Get the currently active admin signup URL.
     *
     * @return String URL
     */
    @Override
    public String getAdminSignupUrl() {
        return ADMIN_SIGNUP_URL;
    }
}
