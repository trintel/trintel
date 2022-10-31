package sopro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import sopro.model.ActionType;
import sopro.model.util.InitiatorType;


public interface ActionTypeRepository extends CrudRepository<ActionType, Long> {
    ActionType findByName(String name);
    List<ActionType> findByInitiatorType(InitiatorType initiatorType);

    @Query("SELECT t FROM ActionType t WHERE standardAction = false")
    List<ActionType> getSpecialActionTypes();
}
