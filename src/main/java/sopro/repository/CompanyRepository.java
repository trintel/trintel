package sopro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sopro.model.Company;

// JpaRepository statt CrudRespository für Sortiermethoden
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByName(String name); //? gibt es das??
    List<Company> findByIdNot(Long id);
    // added
    List<Company> findByOrderByNameAsc();
}
