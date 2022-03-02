package sopro.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
  * Defines the class User with all needed entries for the Database.
  */
@Entity
public class User {
    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter	@NotEmpty @Column(unique = true) private String email;
    @Getter @Setter @NotEmpty private String surname;
    @Getter @Setter	@NotEmpty private String forename;
    @Getter @Setter	@NotEmpty private String password;
    @Getter @Setter	@NotEmpty private String role;
    @Getter @Setter	@ManyToOne private Company company;

    public User(){}

    public User(String surname,String forename, String email, String password, Company company){
       this.surname = surname;
       this.forename = forename;
       this.email = email;
       this.password = password;
       this.company = company;
    }

}