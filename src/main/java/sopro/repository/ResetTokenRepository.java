package sopro.repository;

import java.util.Date;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import sopro.model.User;
import sopro.model.ResetToken;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {

    ResetToken findByToken(String token);

    ResetToken findByUser(User user);

    Stream<ResetToken> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    void deleteByUser(User user);
}
