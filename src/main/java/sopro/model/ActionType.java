package sopro.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import sopro.model.util.IdHandler;
import sopro.model.util.InitiatorType;

import lombok.Getter;
import lombok.Setter;

@Entity
public class ActionType {

    @Getter
    @Setter
    @Id
    private Long id;
    @Getter
    @Setter
    @OneToMany(mappedBy = "actiontype")
    private List<Action> actions;
    @Getter
    @Setter
    @NotEmpty
    @Column(unique = true)
    private String name;
    @Getter
    @Setter
    @NotNull
    private InitiatorType initiatorType;
    @Getter
    @Setter
    @Nullable
    private String iconClass;
    @Getter
    @Setter
    @Nullable
    private String colorClass;
    @Getter
    @Setter
    @NotEmpty
    private String text;

    public ActionType() {
        this.id = IdHandler.generateId();
    }

    public ActionType(String name, String text, InitiatorType initiatorType) {
        this.id = IdHandler.generateId();
        this.name = name;
        this.text = text;
        this.initiatorType = initiatorType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ActionType other = (ActionType) obj;
        if (actions == null) {
            if (other.actions != null)
                return false;
        } else if (!actions.equals(other.actions))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (initiatorType != other.initiatorType)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }
}
