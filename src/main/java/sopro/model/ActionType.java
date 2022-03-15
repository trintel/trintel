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
    @Getter @Setter private boolean standardAction = false;

    public ActionType() {
    }

    public ActionType(String name, String text, InitiatorType initiatorType) {
        this.name = name;
        this.text = text;
        this.initiatorType = initiatorType;
    }

    /**
     * @param id
     * @param name
     * @param initiatorType
     * @param text
     * @param standardAction
     */
    public ActionType(Long id, @NotEmpty String name, @NotNull InitiatorType initiatorType, String text,
            boolean standardAction) {
        this.id = id;
        this.name = name;
        this.initiatorType = initiatorType;
        this.text = text;
        this.standardAction = standardAction;
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
        m.put("initiatorType", this.initiatorType.toString());
        m.put("text", this.text);
        m.put("standardAction", this.standardAction);

        return m;
    }
}
