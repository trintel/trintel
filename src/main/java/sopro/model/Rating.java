package sopro.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import sopro.model.util.IdHandler;

@Entity
public class Rating {

    @Id @Getter @Setter private Long id;
    @Getter @ManyToOne private Transaction transaction;
    @Getter @Setter private int stars;
    @Getter @Setter private @ManyToOne Company ratingCompany;
    @Getter @Setter private @ManyToOne Company ratedCompany;

    public Rating() {
        this.id = IdHandler.generateId();
    }

    public Rating(Transaction transaction, Company ratedCompany, Company ratingCompany, int stars) {
        if(stars < 0 || stars > 5) {
            throw new IllegalArgumentException("You can only rate 0-5 stars.");
        }
        this.id = IdHandler.generateId();
        this.transaction = transaction;
        this.ratingCompany = ratingCompany;
        this.ratedCompany = ratedCompany;
        this.stars = stars;
    }

}
