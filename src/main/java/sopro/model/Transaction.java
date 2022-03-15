package sopro.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Entity
public class Transaction {

    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @OneToMany(mappedBy = "transaction") List<Action> actions;
    @Getter @Setter @NotNull @OneToOne private Company buyer;
    @Getter @Setter @NotNull @OneToOne private Company seller;
    @Getter @Setter private String product;
    @Getter @Setter private Boolean completed = false;
    @Getter @Setter private Boolean shipped = false;
    @Getter @Setter private Boolean confirmed = false;

    public Transaction() {}

    public Transaction(Company buyer, Company seller) {
        this.actions = new ArrayList<>();
        this.buyer = buyer;
        this.seller = seller;
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
        m.put("completed", this.completed);
        m.put("shipped", this.shipped);
        m.put("confirmed", this.confirmed);
        return m;
    }
}
