package sopro.model;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Company {
    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @NotEmpty @Column(unique = true) private String name;
    @Getter @Setter private String description;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "company") private List<User> students;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "buyer", fetch = FetchType.EAGER) private List<Transaction> buyingTransactions;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "seller", fetch = FetchType.EAGER) private List<Transaction> sellingTransactions;
    @Getter @Setter	@JsonIgnore	@OneToOne private CompanyLogo companyLogo;

    public Company(){
        this.description = "";
    }

    public Company(String name) {
       this.name = name;
       this.students = new ArrayList<User>();
       this.description = "";
    }

    @PreRemove
    private void preRemove() {
        students.forEach(student -> student.setCompany(null));
    }

}
