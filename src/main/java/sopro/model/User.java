package sopro.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Entity
public class User {
    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
	@Getter @Setter	@NotEmpty @Column(unique = true) private String email;
	@Getter @Setter @NotEmpty private String surname;
	@Getter @Setter	@NotEmpty private String forename;
	@Getter @Setter	@NotEmpty private String password;
    @Getter @Setter	@NotEmpty private String role;

    public User(){}

    public User(String surname,String forename, String email, String password){
       this.surname = surname;
       this.forename = forename;
       this.email = email;
       this.password = password;
    }

}