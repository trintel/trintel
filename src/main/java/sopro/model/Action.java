package sopro.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Action {
    
    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @ManyToOne @NotNull private ActionType actiontype;
    @Getter @Setter @ManyToOne @NotNull private Transaction transaction;
    @Getter @Setter @ManyToOne @OneToOne private User initiator;
    @Getter @Setter private String message;
    @Getter @Setter private Integer amount;
    @Getter @Setter private Double pricePerPiece;
    @Getter final private LocalDate date;       //the time and date of the action
    @Getter final private LocalTime time;

    public Action() {
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    public Action(String message, ActionType actiontype, Transaction transaction) {
        this.message = message;
        this.actiontype = actiontype;
        this.transaction = transaction;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    /**
     * @param id
     * @param actiontype
     * @param transaction
     * @param initiator
     * @param message
     * @param amount
     * @param pricePerPiece
     * @param date
     * @param time
     */
    public Action(Long id, @NotNull ActionType actiontype, @NotNull Transaction transaction, User initiator,
            String message, Integer amount, Double pricePerPiece, LocalDate date, LocalTime time) {
        this.id = id;
        this.actiontype = actiontype;
        this.transaction = transaction;
        this.initiator = initiator;
        this.message = message;
        this.amount = amount;
        this.pricePerPiece = pricePerPiece;
        this.date = date;
        this.time = time;
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
}
