package sopro.repository;

import java.util.List;

import javax.transaction.Transaction;

import org.springframework.data.repository.CrudRepository;

import sopro.model.Action;

public interface ActionRepository extends CrudRepository<Action, Long> {
    List<Action> findByTransaction(Transaction transaction); 
}

