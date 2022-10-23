package sopro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sopro.model.Company;
import sopro.model.Rating;
import sopro.model.Transaction;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByTransaction(Transaction transaction);
    List<Rating> findByRatingCompany(Company company);
    List<Rating> findByRatedCompany(Company company);
}
