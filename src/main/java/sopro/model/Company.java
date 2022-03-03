package sopro.model;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class Company {
    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @NotEmpty private String name;
    @Getter @Setter private String description;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL) private List<User> students;

    public Company(){}
    
    public Company(String name){
       this.name = name;
       this.students = new ArrayList<User>();
       this.description = "";
    }  
}
