package sopro.model;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Getter
    @Setter
    @Id
    private Long id;
    @Getter
    @Setter
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.REMOVE)
    List<Action> actions;
    @Getter
    @Setter
    @NotNull
    @ManyToOne
    private Company buyer;
    @Getter
    @Setter
    @NotNull
    @ManyToOne
    private Company seller;
    @Getter
    @Setter
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.REMOVE)
    List<Rating> ratings;
    @Getter
    @Setter
    private String product;
    @Getter
    @Setter
    private Boolean confirmed = false;
    @Getter
    @Setter
    private Boolean active = true;
    @Getter
    @Setter
    private Boolean buyerArchived = false;
    @Getter
    @Setter
    private Boolean sellerArchived = false;

    @Getter
    @Setter
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.REMOVE)
    private List<NonArchivedTransaction> nonArchivedTransactions;

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
    public Action getLastAction() {
        return actions.get(actions.size() - 1);
    }

    /**
     * get the date of the newest action from this transaction
     *
     * @return
     */
    public LocalDate getLatestActionDate() {
        return this.getLastAction().getDate();
    }

    /**
     * get the time of the newest action from this transaction
     *
     * @return
     */
    public LocalTime getLatestActionTime() {
        return this.getLastAction().getTime();
    }

    /**
     * get the Name of the latest action
     *
     * @return
     */
    public String getLatestActionName() {
        return this.getLastAction().getActiontype().getName();
    }

    /**
     * get the ActionType of the latest action
     *
     * @return
     */
    public ActionType getLatestActionType() {
        return this.getLastAction().getActiontype();
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
        m.put("confirmed", this.confirmed);
        return m;
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
        Transaction other = (Transaction) obj;
        if (actions == null) {
            if (other.actions != null)
                return false;
        } else if (!actions.equals(other.actions))
            return false;
        if (active == null) {
            if (other.active != null)
                return false;
        } else if (!active.equals(other.active))
            return false;
        if (buyer == null) {
            if (other.buyer != null)
                return false;
        } else if (!buyer.equals(other.buyer))
            return false;
        if (confirmed == null) {
            if (other.confirmed != null)
                return false;
        } else if (!confirmed.equals(other.confirmed))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (seller == null) {
            if (other.seller != null)
                return false;
        } else if (!seller.equals(other.seller))
            return false;
        return true;
    }
}
