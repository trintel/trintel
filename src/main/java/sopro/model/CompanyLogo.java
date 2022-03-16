package sopro.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.apache.tomcat.util.codec.binary.Base64;

import sopro.model.util.IdHandler;

import lombok.Getter;
import lombok.Setter;

@Entity
public class CompanyLogo {

    @Getter @Setter @Id Long id;
    @Getter @Setter @Lob byte[] logo;
    @Getter @Setter @OneToOne Company company;

    public CompanyLogo() {
        this.id = IdHandler.generateId();
    }

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", this.id);
        m.put("logo", Base64.encodeBase64String(this.logo));

        if (this.company != null)
            m.put("company", company.getId());
        else
            m.put("company", null); // ! Careful with null.

        return m;
    }
}
