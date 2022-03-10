package sopro.repository;

import org.springframework.data.repository.CrudRepository;

import sopro.model.Company;
import sopro.model.CompanyLogo;

public interface CompanyLogoRepository  extends CrudRepository<CompanyLogo, Long> {
    CompanyLogo findByCompany(Company company);
    CompanyLogo findByCompanyId(Long companyID);
}
