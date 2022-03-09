package sopro.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import sopro.model.ActionType;
import sopro.model.InitiatorType;

public interface ActionTypeRepository extends CrudRepository<ActionType, Long> {
    ActionType findByName(String name);
    List<ActionType> findByInitiatorType(InitiatorType initiatorType);
}
