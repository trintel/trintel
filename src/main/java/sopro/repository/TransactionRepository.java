package sopro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import sopro.model.Company;
import sopro.model.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByBuyer(Company buyer);
    List<Transaction> findBySeller(Company seller);
    @Query("SELECT COUNT(DISTINCT buyer) FROM Transaction t WHERE t.seller=?1 AND confirmed = true")
    Long countDistinctBuyers(Company companySeller);
    @Query("SELECT COUNT(DISTINCT seller) FROM Transaction t WHERE t.buyer=?1 AND confirmed = true")
    Long countDistinctSellers(Company companyBuyer);
    @Query("SELECT COUNT(*) FROM Transaction t WHERE (t.buyer=?1 OR t.seller =?1) AND t.confirmed = true")
    Long countConfirmedTransactions(Company company);
}
