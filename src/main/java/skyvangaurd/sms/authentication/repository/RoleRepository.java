package skyvangaurd.sms.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import skyvangaurd.sms.authentication.model.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRoleName(String name);
}
