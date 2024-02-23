package skyvangaurd.sms.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import skyvangaurd.sms.authentication.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  // Find a user by email
  Optional<User> findByEmail(String email);

  // Check if an email already exists in the database
  boolean existsByEmail(String email);

  // Custom query to find users with something, i.e. active, inactive, createdAt, deleted at and etc.
}
