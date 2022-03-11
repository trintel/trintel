package sopro.model;

import java.util.List;

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
    @Getter @Setter private boolean standartAction = false;

    public ActionType() {}

    public ActionType(String name, String text, InitiatorType initiatorType) {
        this.name = name;
        this.text = text;
        this.initiatorType = initiatorType;
    }



}
