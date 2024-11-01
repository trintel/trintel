package sopro.repository;

import sopro.model.Transaction;

public interface CustomTransactionRepository {

    <S extends Transaction> S create(S entity);

}
