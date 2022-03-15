package sopro.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import sopro.model.util.IdHandler;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Transaction {

    @Getter @Setter @Id private Long id;
    @Getter @Setter @OneToMany(mappedBy = "transaction", cascade = CascadeType.REMOVE) List<Action> actions;
    @Getter @Setter @NotNull @ManyToOne private Company buyer;
    @Getter @Setter @NotNull @ManyToOne private Company seller;
    @Getter @Setter private String product;
    @Getter @Setter private Boolean paid = false;
    @Getter @Setter private Boolean shipped = false;
    @Getter @Setter private Boolean confirmed = false;
    @Getter @Setter private Boolean active = true;

    public Transaction() {
        this.id = IdHandler.generateId();
    }

    public Transaction(Company buyer, Company seller) {
        this.id = IdHandler.generateId();
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
        for (Action action : actions) {
            if (action.getAmount() != null) { // null if not an offer
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
        for (Action action : actions) {
            if (action.getPricePerPiece() != null) { // null if not an offer
                pricePerPiece = action.getPricePerPiece();
            }
        }
        return pricePerPiece;
    }

    /**
     * get the last action from this transaction
     *
     * @return
     */
    public Action getLatestAction() {
        return actions.get(actions.size() - 1);
    }

    /**
     * get the Name of the latest action
     *
     * @return
     */
    public String getLatestActionName() {
        return this.getLatestAction().getActiontype().getName();
    }

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", this.id);
        m.put("buyer", this.buyer.getId());
        m.put("seller", this.seller.getId());
        m.put("product", this.product);
        m.put("active", this.active);
        m.put("shipped", this.shipped);
        m.put("confirmed", this.confirmed);
        m.put("paid", this.paid);
        return m;
    }

}
