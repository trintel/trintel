package sopro.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Entity
public class Transaction {

    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @OneToMany(mappedBy = "transaction") List<Action> actions;
    @Getter @Setter @NotNull @ManyToOne private Company buyer;
    @Getter @Setter @NotNull @ManyToOne private Company seller;
    @Getter @Setter private String product;
    @Getter @Setter private Boolean paid = false;
    @Getter @Setter private Boolean shipped = false;
    @Getter @Setter private Boolean confirmed = false;
    @Getter @Setter private Boolean active = true;

    public Transaction() {}

    public Transaction(Company buyer, Company seller) {
        this.actions = new ArrayList<>();
        this.buyer = buyer;
        this.seller = seller;
    }


    /**
     *
     * @return the amount of the transaction, if set.
     */
    public Integer getAmount() {
        Integer amount = 0;
        for(Action action : actions) {
            if(action.getAmount() != null) {        //null if not an offer
                amount = action.getAmount();
            }
        }
        return amount;
    }

    /**
     *
     * @return the price per piece of the transaction, if set.
     */
    public Double getPricePerPiece() {
        Double pricePerPiece = 0.0;
        for(Action action : actions) {
            if(action.getPricePerPiece() != null) {        //null if not an offer
                pricePerPiece = action.getPricePerPiece();
            }
        }
        return pricePerPiece;
    }

    /**
     * get the last action from this transaction
     * @return
     */
    public Action getLatestAction() {
        return actions.get(actions.size() - 1);
    }


    /**
     * get the Name of the latest action
     * @return
     */
    public String getLatestActionName() {
        return this.getLatestAction().getActiontype().getName();
    }

}
