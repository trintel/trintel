package sopro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import sopro.model.Company;
import sopro.model.Transaction;

public interface TransactionRepository extends CustomTransactionRepository, CrudRepository<Transaction, Long> {
    List<Transaction> findByBuyer(Company buyer);

    List<Transaction> findBySeller(Company seller);

    @Query("SELECT COUNT(DISTINCT buyer) FROM Transaction t WHERE t.seller=?1 AND confirmed = true")
    Long countDistinctBuyers(Company companySeller);

    @Query("SELECT COUNT(DISTINCT seller) FROM Transaction t WHERE t.buyer=?1 AND confirmed = true")
    Long countDistinctSellers(Company companyBuyer);

    @Query("SELECT COUNT(*) FROM Transaction t WHERE (t.buyer=?1 OR t.seller =?1) AND t.confirmed = true")
    Long countConfirmedTransactions(Company company);

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.buyer.name) LIKE LOWER(CONCAT('%', ?1, '%')) AND LOWER(t.seller.name) LIKE LOWER(CONCAT('%', ?2, '%'))")
    List<Transaction> findAllByBuyerAndSellerName(String buyer, String seller);

    @Query("SELECT t FROM NonArchivedTransaction a JOIN Transaction t ON a.transaction=t AND a.company = ?3 WHERE LOWER(t.buyer.name) LIKE LOWER(CONCAT('%', ?1, '%')) AND LOWER(t.seller.name) LIKE LOWER(CONCAT('%', ?2, '%'))")
    List<Transaction> findOwnByBuyerAndSellerName(String buyer, String seller, Company studentCompany);

    @Query("SELECT t FROM Transaction t LEFT JOIN NonArchivedTransaction a ON a.transaction=t AND a.company=?3 WHERE a.id IS NULL AND (t.seller = ?3 OR t.buyer = ?3) AND LOWER(t.buyer.name) LIKE LOWER(CONCAT('%', ?1, '%')) AND LOWER(t.seller.name) LIKE LOWER(CONCAT('%', ?2, '%'))")
    List<Transaction> findOwnByBuyerAndSellerNameArchived(String buyer, String seller, Company studentCompany);
}
