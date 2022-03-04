package sopro.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import sopro.model.Company;
import sopro.model.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByBuyer(Company buyer);
    List<Transaction> findBySeller(Company seller);
}

