package sopro.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import sopro.model.Company;

public interface CompanyRepository extends CrudRepository<Company, Long> {
    Company findByName(String name); //? gibt es das??
    List<Company> findByIdNot(Long id);
}
