package skyvangaurd.sms.authentication.service;

import java.util.List;
import java.util.Optional;

import skyvangaurd.sms.authentication.dto.LoginRequest;
import skyvangaurd.sms.authentication.model.User;

public interface UserService {

  public List<String> getRolesForUser(String username);

  public User registerUser(LoginRequest user);

  public Optional<User> findByEmail(String email);

  public Optional<User> findById(Long id);

  public List<User> getAllUsers();

  public boolean existsByEmail(String email);

}
