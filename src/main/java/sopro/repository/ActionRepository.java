package sopro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import sopro.model.Action;
import sopro.model.Transaction;

public interface ActionRepository extends CrudRepository<Action, Long> {
    List<Action> findByTransaction(Transaction transaction);

    @Query("SELECT max(id) FROM Action")
    Long getNewId();
}
