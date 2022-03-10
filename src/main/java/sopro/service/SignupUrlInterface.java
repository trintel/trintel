package sopro.service;

public interface SignupUrlInterface {
    String generateStudentSignupURL();

    String generateAdminSignupURL();

    String getStudentSignupUrl();

    String getAdminSignupUrl();
}
