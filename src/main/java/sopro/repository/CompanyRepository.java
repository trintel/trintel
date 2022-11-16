package sopro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sopro.model.Company;

// JpaRepository statt CrudRespository für Sortiermethoden
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByName(String name); //? gibt es das??
    List<Company> findByIdNot(Long id);
    // added
    List<Company> findByOrderByNameAsc();

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Company> searchByString(String searchString);

    @Query("SELECT c FROM Company c WHERE NOT c.id = ?2 AND LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Company> searchByStringNotOwn(String searchString, Long id);
}
