package sopro.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Entity
public class ActionType {

    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @OneToMany(mappedBy = "actiontype") private List<Action> actions;
    @Getter @Setter @NotEmpty @Column(unique = true) private String name;
    @Getter @Setter @NotNull private InitiatorType initiatorType;
    @Getter @Setter private String text;

    public ActionType() {}

    public ActionType(String name, String text, InitiatorType initiatorType) {
        this.name = name;
        this.text = text;
        this.initiatorType = initiatorType;
    }

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", this.id);
        // m.put("actions", this.actions); // TODO Felix selbers Problem
        m.put("name", this.name);
        m.put("initiatorType", this.initiatorType.toString());
        m.put("text", this.text);
        
        return m;
    }

}
