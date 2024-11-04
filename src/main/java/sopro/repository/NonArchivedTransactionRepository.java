package sopro.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import sopro.model.Company;
import sopro.model.NonArchivedTransaction;
import sopro.model.Transaction;

@Repository
public interface NonArchivedTransactionRepository extends CrudRepository<NonArchivedTransaction, Long> {

    NonArchivedTransaction findByTransactionAndCompany(Transaction transaction, Company company);

    List<NonArchivedTransaction> findByTransaction(Transaction transaction);

}
