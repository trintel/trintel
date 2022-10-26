package sopro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sopro.model.Company;
import sopro.model.Rating;
import sopro.model.Transaction;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByTransaction(Transaction transaction);
    List<Rating> findByRatingCompany(Company company);
    List<Rating> findByRatedCompany(Company company);

    @Query("SELECT AVG(stars) FROM Rating WHERE ratedCompany.id = ?1")
    Optional<Double> getAverageById(Long companyId);
}
