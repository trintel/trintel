package sopro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.Company;
import sopro.model.User;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;

@Controller
public class StudentController {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    CompanyRepository companyRepository;

    @GetMapping("/students")
    public String listAllStudents(Model model) {
        model.addAttribute("students", userRepository.findByRole("STUDENT")); // list all students
        return "students-list";
    }

    @GetMapping("/student/{id}/reassign")
    public String editStudent(Model model, @PathVariable Long id) {
        User student = userRepository.findById(id).get();
        model.addAttribute("student", student);
        if (student.getCompany() == null) {
            model.addAttribute("companies", companyRepository.findAll());
        } else {
            model.addAttribute("companies", companyRepository.findByIdNot(student.getCompany().getId())); // get all
                                                                                                          // companies
                                                                                                          // except for
                                                                                                          // the current
                                                                                                          // one
        }
        model.addAttribute("studentID", id); // add the student id to the model (for post-request navigation)
        return "student-reassign";
    }

    @PostMapping("/student/{id}/reassign")
    public String moveToCompany(String companyName, @PathVariable Long id, Model model) {
        User user = userRepository.findById(id).get(); // find the student to be editet
        Company company2 = companyRepository.findByName(companyName); // find the new company
        user.setCompany(company2);
        userRepository.save(user);
        return "redirect:/students";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students/search/{searchString}")
    public String searchStudents(@PathVariable String searchString, Model model) {
        model.addAttribute("students", userRepository.searchByString(searchString)); // add a list of all students based on the searchstring
        return "students-list";
    }
    
}
