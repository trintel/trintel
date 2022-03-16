package sopro.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import sopro.model.util.IdHandler;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Action {

    @Getter @Setter @Id private Long id;
    @Getter @Setter @ManyToOne @NotNull private ActionType actiontype;
    @Getter @Setter @ManyToOne @NotNull private Transaction transaction;
    @Getter @Setter @ManyToOne @OneToOne private User initiator;
    @Getter @Setter private String message;
    @Getter @Setter private Integer amount;
    @Getter @Setter private Double pricePerPiece;
    @Getter @Setter private LocalDate date;       //the time and date of the action
    @Getter @Setter private LocalTime time;

    public Action() {
        this.id = IdHandler.generateId();
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    public Action(String message, ActionType actiontype, Transaction transaction) {
        this.id = IdHandler.generateId();
        this.message = message;
        this.actiontype = actiontype;
        this.transaction = transaction;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", this.id);
        m.put("actionType", this.actiontype.getId());
        m.put("transaction", this.transaction.getId());
        m.put("initiator", this.initiator.getId());
        m.put("message", this.message);
        m.put("amount", this.amount);
        m.put("pricePerPiece", this.pricePerPiece);
        m.put("date", this.date.toString());
        m.put("time", this.time.toString());

        return m;
    }

    /* (non-Javadoc)
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
        Action other = (Action) obj;
        if (actiontype == null) {
            if (other.actiontype != null)
                return false;
        } else if (!actiontype.equals(other.actiontype))
            return false;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (initiator == null) {
            if (other.initiator != null)
                return false;
        } else if (!initiator.equals(other.initiator))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (pricePerPiece == null) {
            if (other.pricePerPiece != null)
                return false;
        } else if (!pricePerPiece.equals(other.pricePerPiece))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        if (transaction == null) {
            if (other.transaction != null)
                return false;
        } else if (!transaction.equals(other.transaction))
            return false;
        return true;
    }
}
