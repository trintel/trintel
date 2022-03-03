package sopro.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import sopro.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByRole(String role);
}
