package sopro.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Company {
    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @NotEmpty @Column(unique = true) private String name;
    @Getter @Setter private String description;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL) private List<User> students;

    public Company(){}

    public Company(String name){
       this.name = name;
       this.students = new ArrayList<User>();
       this.description = "";
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
