package sopro.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Company {
    @Getter @Setter @Id @GeneratedValue(strategy = GenerationType.AUTO)	private Long id;
    @Getter @Setter @NotEmpty @Column(unique = true) private String name;
    @Getter @Setter private String description;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "company") private List<User> students;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "buyer", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) private List<Transaction> buyingTransactions;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "seller", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) private List<Transaction> sellingTransactions;
    @Getter @Setter	@JsonIgnore	@OneToOne private CompanyLogo companyLogo;

    public Company(){
        this.description = "";
    }

    public Company(String name) {
       this.name = name;
       this.students = new ArrayList<User>();
       this.description = "";
    }

    @PreRemove
    private void preRemove() {
        students.forEach(student -> student.setCompany(null));
    }

    /**
     * Returns a map of all fields.
     *
     * @return m map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", this.id);
        m.put("name", this.name);
        m.put("description", this.description);
        return m;
    }

    public List<Company> getRelatedCompanies() {
        List<Company> companies = new ArrayList<>();
        for(Transaction transaction : buyingTransactions) {
            if(!companies.contains(transaction.getSeller())) {
                companies.add(transaction.getSeller());
            }
        }
        for(Transaction transaction : sellingTransactions) {
            if(!companies.contains(transaction.getBuyer())) {
                companies.add(transaction.getBuyer());
            }
        }
        return companies;
    }

    public Integer getConfirmedTransactionsWith(Company company) {
        Integer nb_transactions = 0;
        for(Transaction transaction : buyingTransactions) {
            if(transaction.getConfirmed()) {
                nb_transactions += 1;
            }
        }
        for(Transaction transaction : sellingTransactions) {
            if(transaction.getConfirmed()) {
                nb_transactions += 1;
            }
        }
        return nb_transactions;
    }

    public Integer getNumberOffers(Company company) {
        Integer nb_offers = 0;
        for(Transaction transaction : buyingTransactions) {
            nb_offers += transaction.getActions().stream().filter(a -> a.getActiontype().getName().equals("Offer")).toArray(Action[] :: new).length;
        }
        for(Transaction transaction : sellingTransactions) {
            nb_offers += transaction.getActions().stream().filter(a -> a.getActiontype().getName().equals("Offer")).toArray(Action[] :: new).length;
        }
        return nb_offers;
    }

    public Integer getNumberTradedProducts(Company company) {
        Set<String> products = new HashSet<>();
        for(Transaction transaction : buyingTransactions) {
            products.add(transaction.getProduct());
        }
        for(Transaction transaction : sellingTransactions) {
            products.add(transaction.getProduct());
        }
        return products.size();
    }
}
