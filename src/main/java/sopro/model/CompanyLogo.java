package sopro.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
public class CompanyLogo {

    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    @Getter @Setter @Lob byte[] logo;
    @Getter @Setter @OneToOne(cascade = CascadeType.REMOVE) Company company;

}
