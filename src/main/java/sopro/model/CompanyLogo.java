package sopro.model;

import java.util.HashMap;
import java.util.Map;

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
    @Getter @Setter @OneToOne Company company;

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", this.id);
        m.put("logo", this.logo);
        m.put("company", this.company);
        return m;
    }
}
