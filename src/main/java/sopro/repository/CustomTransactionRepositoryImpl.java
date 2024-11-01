package sopro.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import sopro.model.NonArchivedTransaction;
import sopro.model.Transaction;

@Repository
public class CustomTransactionRepositoryImpl implements CustomTransactionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public <S extends Transaction> S create(S entity) {
        // Save the transaction using the standard repository method
        entityManager.persist(entity);

        // Create an entry in the nonarchivedtransaction table for the buyer
        // Check if a non-archived transaction already exists for the buyer
        String queryBuyer = "SELECT COUNT(n) FROM NonArchivedTransaction n WHERE n.transaction = :transaction AND n.company = :company";
        Long countBuyer = entityManager.createQuery(queryBuyer, Long.class)
                .setParameter("transaction", entity)
                .setParameter("company", entity.getBuyer())
                .getSingleResult();

        if (countBuyer == 0) {
            NonArchivedTransaction nonArchivedTransactionBuyer = new NonArchivedTransaction();
            nonArchivedTransactionBuyer.setTransaction(entity);
            nonArchivedTransactionBuyer.setCompany(entity.getBuyer());
            entityManager.persist(nonArchivedTransactionBuyer);
        }

        // Check if a non-archived transaction already exists for the seller
        String querySeller = "SELECT COUNT(n) FROM NonArchivedTransaction n WHERE n.transaction = :transaction AND n.company = :company";
        Long countSeller = entityManager.createQuery(querySeller, Long.class)
                .setParameter("transaction", entity)
                .setParameter("company", entity.getSeller())
                .getSingleResult();

        if (countSeller == 0) {
            NonArchivedTransaction nonArchivedTransactionSeller = new NonArchivedTransaction();
            nonArchivedTransactionSeller.setTransaction(entity);
            nonArchivedTransactionSeller.setCompany(entity.getSeller());
            entityManager.persist(nonArchivedTransactionSeller);
        }

        return entity;
    }
}
