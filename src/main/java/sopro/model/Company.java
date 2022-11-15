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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.validation.constraints.NotEmpty;

import sopro.model.util.IdHandler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Company {

    @Getter @Setter @Id private Long id;
    @Getter @Setter @NotEmpty @Column(unique = true) private String name;
    @Getter @Setter private String description;
    @Getter @Setter private String homepage;
    @Setter	@JsonIgnore	@OneToMany(mappedBy = "company") private List<User> students;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) private List<Transaction> buyingTransactions;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) private List<Transaction> sellingTransactions;
    @Getter @Setter	@JsonIgnore	@OneToOne(mappedBy = "company") private CompanyLogo companyLogo;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "ratingCompany", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) private List<Rating> outRatings;
    @Getter @Setter	@JsonIgnore	@OneToMany(mappedBy = "ratedCompany", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) private List<Rating> inRatings;

    public Company() {
        this.id = IdHandler.generateId();
        this.description = "";
        this.homepage = "";
    }

    public Company(String name) {
        this.id = IdHandler.generateId();
        this.name = name;
        this.students = new ArrayList<User>();
        this.description = "";
        this.homepage = "";
    }

    public List<User> getStudents() {
        List<User> nonLockedStudents = new ArrayList<>();
        for(User student : this.students) {
            if(student.isAccountNonLocked()) {
                nonLockedStudents.add(student);
            }
        }
        return nonLockedStudents;
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
                if(transaction.getSeller().equals(company)) nb_transactions += 1;
            }
        }
        for(Transaction transaction : sellingTransactions) {
            if(transaction.getConfirmed()) {
                if(transaction.getBuyer().equals(company)) nb_transactions += 1;
            }
        }
        return nb_transactions;
    }

    public Integer getNumberOffers(Company company) {
        Integer nb_offers = 0;
        for(Transaction transaction : buyingTransactions) {
            if(transaction.getSeller().equals(company)) nb_offers += transaction.getActions().stream().filter(a -> a.getActiontype().getName().equals("Offer")).toArray(Action[] :: new).length;
        }
        for(Transaction transaction : sellingTransactions) {
            if(transaction.getBuyer().equals(company)) nb_offers += transaction.getActions().stream().filter(a -> a.getActiontype().getName().equals("Offer")).toArray(Action[] :: new).length;
        }
        return nb_offers;
    }

    public Integer getNumberTradedProducts(Company company) {
        Set<String> products = new HashSet<>();
        for(Transaction transaction : buyingTransactions) {
            if(transaction.getSeller().equals(company)) products.add(transaction.getProduct());
        }
        for(Transaction transaction : sellingTransactions) {
            if(transaction.getBuyer().equals(company)) products.add(transaction.getProduct());
        }
        return products.size();
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
        Company other = (Company) obj;
        if (buyingTransactions == null) {
            if (other.buyingTransactions != null)
                return false;
        } else if (!buyingTransactions.equals(other.buyingTransactions))
            return false;
        if (companyLogo == null) {
            if (other.companyLogo != null)
                return false;
        } else if (!companyLogo.equals(other.companyLogo))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sellingTransactions == null) {
            if (other.sellingTransactions != null)
                return false;
        } else if (!sellingTransactions.equals(other.sellingTransactions))
            return false;
        if (students == null) {
            if (other.students != null)
                return false;
        } else if (!students.equals(other.students))
            return false;
        return true;
    }
}
