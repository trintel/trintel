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

    @Getter @Setter @Id private Long id;
    @Getter @Setter @OneToMany(mappedBy = "transaction", cascade = CascadeType.REMOVE) List<Action> actions;
    @Getter @Setter @NotNull @ManyToOne private Company buyer;
    @Getter @Setter @NotNull @ManyToOne private Company seller;
    @Getter @Setter @OneToMany(mappedBy = "transaction") List<Rating> ratings;
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
     * get the last standard action from this transaction
     *
     * @return
     */
    public Action getLatestStandardAction() {
        int index = actions.size() - 1;
        while(!actions.get(index).getActiontype().isStandardAction()) {
            index--;
        }
        return actions.get(index);
    }

    /**
     * get the date of the newest action from this transaction
     *
     * @return
     */
    public LocalDate getLatestActionDate() {
        return this.getLatestAction().getDate();
    }

    /**
     * get the time of the newest action from this transaction
     *
     * @return
     */
    public LocalTime getLatestActionTime() {
        return this.getLatestAction().getTime();
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
        if (paid == null) {
            if (other.paid != null)
                return false;
        } else if (!paid.equals(other.paid))
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
        if (shipped == null) {
            if (other.shipped != null)
                return false;
        } else if (!shipped.equals(other.shipped))
            return false;
        return true;
    }
}
