package sopro.repository;

import org.springframework.data.repository.CrudRepository;

import sopro.model.ActionType;

public interface ActionTypeRepository extends CrudRepository<ActionType, Long> {
    ActionType findByName(String name);
}
