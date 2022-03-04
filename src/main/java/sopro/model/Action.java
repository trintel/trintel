package sopro.model;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Action {

    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @ManyToOne @NotNull private ActionType actiontype;
    @Getter @Setter @ManyToOne @NotNull private Transaction transaction;
    @Getter @Setter private String message;
    @Getter final private LocalDate date;       //the time and date of the action
    @Getter final private LocalTime time;


    public Action() {
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    public Action(String message, ActionType actiontype, Transaction transaction){
        this.message = message;
        this.actiontype = actiontype;
        this.transaction = transaction;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }
}
