package sopro.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
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
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "buyer", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) private List<Transaction> buyingTransactions;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "seller", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) private List<Transaction> sellingTransactions;
    @Getter @Setter	@JsonIgnore	@OneToOne private CompanyLogo companyLogo;

    public Company() {
        this.description = "";
    }

    public Company(String name) {
        this.name = name;
        this.students = new ArrayList<User>();
        this.description = "";
    }

    /**
     * @param id
     * @param name
     * @param description
     */
    public Company(Long id, @NotEmpty String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @PreRemove
    private void preRemove() {
        students.forEach(student -> student.setCompany(null));
    }

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", this.id);
        m.put("name", this.name);
        m.put("description", this.description);
        return m;
    }
}
