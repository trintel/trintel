package sopro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import sopro.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.role = ?1 AND accountNonLocked = true")
    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.forename, ' ', u.surname)) LIKE LOWER(CONCAT('%', ?1, '%')) AND u.role = 'STUDENT' AND accountNonLocked = true")
    List<User> searchByString(String searchString);

    @Query("SELECT max(id) FROM User")
    Long getMaxId();
}
