package skyvangaurd.sms.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyvangaurd.sms.authentication.dto.LoginRequest;
import skyvangaurd.sms.authentication.model.Role;
import skyvangaurd.sms.authentication.model.User;
import skyvangaurd.sms.authentication.repository.RoleRepository;
import skyvangaurd.sms.authentication.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Transactional(readOnly = true)
    public List<String> getRolesForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Forbidden access: " + email));

        return user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User registerUser(LoginRequest user) {
        if (existsByEmail(user.email())) {
            throw new IllegalArgumentException("User with email: " + user.email() + " already exists");
        }

        Date today = whatIsToday();
        User newUser = new User();
        newUser.setEmail(user.email());
        newUser.setPassword(user.password());
        newUser.setIsActive(true);
        newUser.setCreatedAt(today);
        newUser.setUpdatedAt(today);

        String hashedPassword = passwordEncoder.encode(user.password());
        newUser.setPassword(hashedPassword);

        // Ensure user's roles are properly managed
        Set<Role> managedRoles = new HashSet<>();
        for (Role role : user.roles()) {
            Role managedAuthority = roleRepository.findByRoleName(role.getRoleName())
                    .orElseGet(() -> roleRepository.save(role));
            managedRoles.add(managedAuthority);
        }

        newUser.setRoles(managedRoles);

        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Date whatIsToday() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = localDate.format(formatter);
        LocalDate localDateFormatted = LocalDate.parse(formattedDate, formatter);
        return Date.from(localDateFormatted.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
